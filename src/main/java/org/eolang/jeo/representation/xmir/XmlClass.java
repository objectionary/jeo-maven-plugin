/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.DirectivesClassProperties;
import org.w3c.dom.Node;
import org.xembly.Directives;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * XML class.
 *
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings("PMD.TooManyMethods")
public final class XmlClass {

    /**
     * Class node from entire XML.
     */
    @ToString.Include
    private final XmlNode node;

    /**
     * Constructor.
     *
     * @param lines XML lines.
     */
    public XmlClass(final String... lines) {
        this(new XMLDocument(String.join("\n", lines)));
    }

    /**
     * Constructor.
     *
     * @param xmlnode XML node.
     */
    public XmlClass(final XML xmlnode) {
        this(xmlnode.node().getFirstChild());
    }

    /**
     * Constructor.
     *
     * @param classname Class name.
     */
    public XmlClass(final String classname) {
        this(XmlClass.empty(classname));
    }

    /**
     * Constructor.
     *
     * @param properties Class properties.
     */
    public XmlClass(final DirectivesClassProperties properties) {
        this("HelloWorld", properties);
    }

    /**
     * Constructor.
     *
     * @param classname Class name.
     * @param properties Class properties.
     */
    public XmlClass(final String classname, final DirectivesClassProperties properties) {
        this(XmlClass.withProps(classname, properties));
    }

    /**
     * Constructor.
     *
     * @param xml Class node.
     */
    public XmlClass(final Node xml) {
        this(new XmlNode(xml));
    }

    /**
     * Constructor.
     *
     * @param node Class node.
     */
    public XmlClass(final XmlNode node) {
        this.node = node;
    }

    /**
     * Class name.
     *
     * @return Name.
     */
    public String name() {
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
     *
     * @return Annotations node.
     */
    public Optional<XmlAnnotations> annotations() {
        return this.node.children()
            .filter(o -> o.hasAttribute("name", "annotations"))
            .filter(o -> o.hasAttribute("base", "tuple"))
            .findFirst()
            .map(XmlAnnotations::new);
    }

    /**
     * Class properties.
     *
     * @return Class properties.
     */
    public XmlClassProperties properties() {
        return new XmlClassProperties(this.node);
    }

    /**
     * Retrieve all constructors from XMIR.
     *
     * @return List of constructors.
     */
    public List<XmlMethod> constructors() {
        return this.methods()
            .stream()
            .filter(XmlMethod::isConstructor)
            .collect(Collectors.toList());
    }

    /**
     * Methods.
     *
     * @return Class methods.
     */
    public List<XmlMethod> methods() {
        return this.node.children()
            .filter(o -> o.attribute("base").isEmpty())
            .map(XmlMethod::new)
            .collect(Collectors.toList());
    }

    /**
     * Fields.
     *
     * @return Class fields.
     */
    public List<XmlField> fields() {
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
    public Optional<XmlAttributes> attributes() {
        return this.node.children()
            .filter(o -> o.hasAttribute("name", "attributes"))
            .filter(o -> o.hasAttribute("base", "tuple"))
            .findFirst()
            .map(XmlAttributes::new);
    }

    /**
     * Copies current class with replaced methods.
     *
     * @param methods Methods.
     * @return Class node.
     */
    public XmlClass replaceMethods(final XmlMethod... methods) {
        return this.withoutMethods().withMethods(methods);
    }

    /**
     * Copies the same class node, but with added methods.
     *
     * @param methods Methods.
     * @return Copy of the class with added methods.
     */
    public XmlClass withMethods(final XmlMethod... methods) {
        return new XmlClass(
            new Xembler(
                Arrays.stream(methods)
                    .map(XmlMethod::toDirectives)
                    .reduce(
                        new Directives(),
                        Directives::append
                    )
            ).applyQuietly(this.node.node())
        );
    }

    /**
     * Copies the same class node, but without methods.
     *
     * @return Class node.
     */
    public XmlClass withoutMethods() {
        return new XmlClass(
            new Xembler(
                new Directives()
                    .xpath("./o[not(@base) and @name]")
                    .remove()
            ).applyQuietly(this.node.node())
        );
    }

    /**
     * Convert XmlClass to XML node.
     *
     * @return XML node.
     */
    public XML toXml() {
        return new XMLDocument(this.node.node());
    }

    /**
     * Generate empty class node with given name.
     *
     * @param classname Class name.
     * @return Class node.
     */
    private static Node empty(final String classname) {
        return XmlClass.withProps(classname, new DirectivesClassProperties(0));
    }

    /**
     * Generate class node with given name and access.
     *
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
