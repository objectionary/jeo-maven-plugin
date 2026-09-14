/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XMLDocument;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeAttributes;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.DirectivesClassProperties;
import org.objectweb.asm.Opcodes;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * XML representation of a Java class from XMIR.
 *
 * <p>This class provides functionality to parse and convert XMIR (EO XML representation) class
 * nodes into bytecode classes. It handles extraction of class properties, methods, fields,
 * annotations, and attributes from the XML structure.</p>
 *
 * @since 0.1.0
 */
public final class XmlClass {

    /**
     * Class node from entire XML.
     */
    private final XmlGlobalObject node;

    /**
     * Package name.
     */
    private final String pckg;

    /**
     * Constructor.
     *
     * @param classname The class name
     */
    XmlClass(final String classname) {
        this("", XmlClass.empty(classname));
    }

    /**
     * Constructor.
     *
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
     * Constructor.
     *
     * @param pckg Package name
     * @param node The XML node representing the class
     */
    XmlClass(final String pckg, final XmlNode node) {
        this.pckg = pckg;
        this.node = new XmlGlobalObject(node);
    }

    /**
     * Convert to bytecode.
     *
     * @return Bytecode class
     */
    public BytecodeClass bytecode() {
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
                this.properties().bytecode()
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

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof XmlClass) {
            final XmlClass clazz = (XmlClass) other;
            result = Objects.equals(this.node, clazz.node)
                && Objects.equals(this.pckg, clazz.pckg);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.node, this.pckg);
    }

    @Override
    public String toString() {
        return String.format("XmlClass(node=%s, pckg=%s)", this.node, this.pckg);
    }

    private Optional<XmlAnnotations> annotations() {
        return this.node.children()
            .map(XmlJeoObject::new)
            .filter(XmlJeoObject::named)
            .filter(object -> "annotations".equals(object.name()))
            .findFirst()
            .map(XmlAnnotations::new);
    }

    private XmlClassProperties properties() {
        return new XmlClassProperties(this.node);
    }

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

    private List<XmlMethod> methods() {
        return this.node.children()
            .map(XmlMethod::new)
            .filter(XmlMethod::isMethod)
            .collect(Collectors.toList());
    }

    private List<XmlField> fields() {
        return this.node.children()
            .map(XmlField::new)
            .filter(XmlField::isField)
            .collect(Collectors.toList());
    }

    private Optional<XmlAttributes> attributes() {
        return this.node.children()
            .map(XmlSeq::new)
            .filter(XmlSeq::named)
            .filter(seq -> "attributes".equals(seq.name()))
            .findFirst()
            .map(XmlAttributes::new);
    }

    private static XmlNode empty(final String classname) {
        return XmlClass.withProps(classname, "", new DirectivesClassProperties(Opcodes.ACC_PUBLIC));
    }

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
