/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XMLDocument;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.DirectivesClassProperties;
import org.objectweb.asm.Opcodes;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * XML representation of a Java class from XMIR.
 *
 * <p>This class provides functionality to parse and convert XMIR (EO XML representation)
 * class nodes into bytecode classes. It handles extraction of class properties,
 * methods, fields, annotations, and attributes from the XML structure.</p>
 * @since 0.1.0
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class XmlClass {

    /**
     * Class node from entire XML.
     */
    @ToString.Include
    private final XmlGlobalObject node;

    /**
     * Package name.
     */
    private final String pckg;

    /**
     * Constructor.
     * @param pckg Package name
     * @param node The XML node representing the class
     */
    XmlClass(final String pckg, final XmlNode node) {
        this.pckg = pckg;
        this.node = new XmlGlobalObject(node);
    }

    /**
     * Constructor.
     * @param classname The class name
     */
    XmlClass(final String classname) {
        this("", XmlClass.empty(classname));
    }

    /**
     * Constructor.
     * @param classname The class name
     * @param sign The class signature
     * @param properties The class properties
     */
    XmlClass(
        final String classname,
        final String sign,
        final DirectivesClassProperties properties
    ) {
        this("", XmlClass.withProps(classname, sign, properties));
    }

    /**
     * Convert to bytecode.
     * @return Bytecode class.
     */
    public BytecodeClass bytecode() {
        final BytecodeClassProperties props = this.properties().bytecode();
        try {
            return new BytecodeClass(
                new ClassName(
                    new PrefixedName(new ClassName(this.pckg, this.name()).full()).decode()
                ),
                this.methods().stream().map(XmlMethod::bytecode)
                    .collect(Collectors.toList()),
                this.fields().stream()
                    .map(XmlField::bytecode)
                    .collect(Collectors.toList()),
                this.annotations()
                    .map(XmlAnnotations::bytecode)
                    .orElse(new BytecodeAnnotations()),
                this.attributes()
                    .map(XmlAttributes::attributes)
                    .orElseGet(BytecodeAttributes::new),
                props
            );
        } catch (final IllegalStateException exception) {
            throw new ParsingException(
                String.format(
                    "Unexpected exception during parsing the class '%s'",
                    this.name()
                ),
                exception
            );
        }
    }

    /**
     * Annotations.
     * @return Annotations node.
     */
    private Optional<XmlAnnotations> annotations() {
        return this.node.children()
            .map(XmlJeoObject::new)
            .filter(XmlJeoObject::named)
            .filter(object -> "annotations".equals(object.name()))
            .findFirst()
            .map(XmlAnnotations::new);
    }

    /**
     * Class properties.
     * @return Class properties.
     */
    private XmlClassProperties properties() {
        return new XmlClassProperties(this.node);
    }

    /**
     * Class name.
     * @return Name.
     */
    private String name() {
        return this.node.attribute("name").orElseThrow(
            () -> new IllegalStateException(
                String.format(
                    "Class name is not defined, expected attribute 'name' in %s",
                    this.node
                )
            )
        );
    }

    /**
     * Methods.
     * @return Class methods.
     */
    private List<XmlMethod> methods() {
        return this.node.children()
            .map(XmlMethod::new)
            .filter(XmlMethod::isMethod)
            .collect(Collectors.toList());
    }

    /**
     * Fields.
     * @return Class fields.
     */
    private List<XmlField> fields() {
        return this.node.children()
            .map(XmlField::new)
            .filter(XmlField::isField)
            .collect(Collectors.toList());
    }

    /**
     * Attributes.
     * @return Attributes.
     */
    private Optional<XmlAttributes> attributes() {
        return this.node.children()
            .map(XmlSeq::new)
            .filter(XmlSeq::named)
            .filter(seq -> "attributes".equals(seq.name()))
            .findFirst()
            .map(XmlAttributes::new);
    }

    /**
     * Generate empty class node with given name.
     * @param classname Class name.
     * @return Class node.
     */
    private static XmlNode empty(final String classname) {
        return XmlClass.withProps(classname, "", new DirectivesClassProperties(Opcodes.ACC_PUBLIC));
    }

    /**
     * Generate class node with given name and access.
     * @param classname Class name.
     * @param sign Class signature.
     * @param props Class properties.
     * @return Class node.
     */
    private static XmlNode withProps(
        final String classname, final String sign, final DirectivesClassProperties props
    ) {
        return new NativeXmlNode(
            new XMLDocument(
                new Xembler(
                    new DirectivesClass(classname, sign, props),
                    new Transformers.Node()
                ).xmlQuietly()
            ).deepCopy().getFirstChild()
        );
    }
}
