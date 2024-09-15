/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XMLDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.DirectivesClassProperties;
import org.objectweb.asm.Opcodes;
import org.w3c.dom.Node;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * XML class.
 *
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class XmlClass {
    /**
     * Class node from entire XML.
     */
    @ToString.Include
    private final XmlNode node;

    /**
     * Constructor.
     * @param node Class node.
     */
    public XmlClass(final XmlNode node) {
        this.node = node;
    }

    /**
     * Constructor.
     * @param classname Class name.
     */
    XmlClass(final String classname) {
        this(XmlClass.empty(classname));
    }

    /**
     * Constructor.
     * @param classname Class name.
     * @param properties Class properties.
     */
    XmlClass(final String classname, final DirectivesClassProperties properties) {
        this(XmlClass.withProps(classname, properties));
    }

    /**
     * Constructor.
     * @param xml Class node.
     */
    XmlClass(final Node xml) {
        this(new XmlNode(xml));
    }

    /**
     * Convert to bytecode.
     * @return Bytecode class.
     */
    public BytecodeClass bytecode() {
        return new BytecodeClass(
            new PrefixedName(this.name()).decode(),
            this.methods().stream().map(XmlMethod::bytecode)
                .collect(Collectors.toList()),
            this.fields().stream()
                .map(XmlField::bytecode)
                .collect(Collectors.toList()),
            this.annotations()
                .map(XmlAnnotations::bytecode)
                .orElse(new BytecodeAnnotations())
                .annotations(),
            this.attributes()
                .map(XmlAttributes::attributes)
                .orElse(new ArrayList<>(0)),
            this.properties().bytecode()
        );
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
     * Annotations.
     * @return Annotations node.
     */
    private Optional<XmlAnnotations> annotations() {
        return this.node.children()
            .filter(o -> o.hasAttribute("name", "annotations"))
            .filter(o -> o.hasAttribute("base", "tuple"))
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
     * Methods.
     * @return Class methods.
     */
    private List<XmlMethod> methods() {
        return this.node.children()
            .filter(o -> !o.attribute("base").isPresent())
            .map(XmlMethod::new)
            .collect(Collectors.toList());
    }

    /**
     * Fields.
     * @return Class fields.
     */
    private List<XmlField> fields() {
        return this.node.children()
            .filter(o -> o.attribute("base").isPresent())
            .filter(o -> "field".equals(o.attribute("base").get()))
            .map(XmlField::new)
            .collect(Collectors.toList());
    }

    /**
     * Attributes.
     * @return Attributes.
     */
    private Optional<XmlAttributes> attributes() {
        return this.node.children()
            .filter(o -> o.hasAttribute("name", "attributes"))
            .filter(o -> o.hasAttribute("base", "tuple"))
            .findFirst()
            .map(XmlAttributes::new);
    }

    /**
     * Generate empty class node with given name.
     * @param classname Class name.
     * @return Class node.
     */
    private static Node empty(final String classname) {
        return XmlClass.withProps(classname, new DirectivesClassProperties(Opcodes.ACC_PUBLIC));
    }

    /**
     * Generate class node with given name and access.
     * @param classname Class name.
     * @param props Class properties.
     * @return Class node.
     */
    private static Node withProps(final String classname, final DirectivesClassProperties props) {
        return new XMLDocument(
            new Xembler(
                new DirectivesClass(classname, props),
                new Transformers.Node()
            ).xmlQuietly()
        ).node().getFirstChild();
    }
}
