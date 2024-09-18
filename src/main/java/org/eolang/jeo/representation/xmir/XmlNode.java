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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eolang.jeo.representation.directives.JeoFqn;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML smart element.
 * Utility class that simplifies work with XML.
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class XmlNode {

    /**
     * Parent node.
     */
    private final Node node;

    /**
     * Constructor.
     * @param xml XML string.
     */
    public XmlNode(final String xml) {
        this(new XMLDocument(xml).node().getFirstChild());
    }

    /**
     * Constructor.
     * @param parent Parent node.
     */
    public XmlNode(final Node parent) {
        this.node = parent;
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean res;
        if (obj instanceof XmlNode) {
            res = new XMLDocument(this.node).equals(new XMLDocument(((XmlNode) obj).node));
        } else {
            res = false;
        }
        return res;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.node);
    }

    @Override
    public String toString() {
        return new XMLDocument(this.node).toString();
    }

    /**
     * Get all child nodes.
     * @return Child nodes.
     */
    public Stream<XmlNode> children() {
        return this.objects().map(XmlNode::new);
    }

    /**
     * Retrieve node text content.
     * @return Text content.
     */
    public String text() {
        return this.node.getTextContent();
    }

    /**
     * Get attribute.
     * @param name Attribute name.
     * @return Attribute.
     */
    public Optional<String> attribute(final String name) {
        final Optional<String> result;
        final NamedNodeMap attrs = this.node.getAttributes();
        if (attrs == null) {
            result = Optional.empty();
        } else {
            result = Optional.ofNullable(attrs.getNamedItem(name)).map(Node::getTextContent);
        }
        return result;
    }

    /**
     * Get child node.
     * @param name Child node name.
     * @return Child node.
     */
    XmlNode child(final String name) {
        return this.optchild(name).orElseThrow(() -> this.notFound(name));
    }

    /**
     * Find elements by xpath.
     * @param xpath XPath.
     * @return List of elements.
     */
    List<String> xpath(final String xpath) {
        return new XMLDocument(this.node).xpath(xpath);
    }

    /**
     * Get child node by attribute.
     * @param attribute Attribute name.
     * @param value Attribute value.
     * @return Child node.
     */
    XmlNode child(final String attribute, final String value) {
        return this.children()
            .filter(xmlnode -> xmlnode.hasAttribute(attribute, value))
            .findFirst()
            .orElseThrow(
                () -> this.notFound(
                    String.format("object with attribute %s='%s'", attribute, value)
                )
            );
    }

    /**
     * Get optional child node by attribute.
     * @param attribute Attribute name.
     * @param value Attribute value.
     * @return Child node.
     */
    Optional<XmlNode> optchild(final String attribute, final String value) {
        return this.children()
            .filter(xmlnode -> xmlnode.hasAttribute(attribute, value))
            .findFirst();
    }

    /**
     * Get first child.
     * @return First child node.
     */
    XmlNode firstChild() {
        return this.children().findFirst()
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "Can't find any child nodes in '%s'",
                        new XMLDocument(this.node)
                    )
                )
            );
    }

    /**
     * Check if an attribute exists.
     * @param name Attribute name.
     * @param value Attribute value.
     * @return True if an attribute with specified value exists.
     */
    boolean hasAttribute(final String name, final String value) {
        return this.attribute(name)
            .map(String::valueOf)
            .map(val -> val.equals(value))
            .orElse(false);
    }

    /**
     * Convert to class.
     * @return Class.
     */
    XmlClass toClass() {
        return new XmlClass(this.node);
    }

    /**
     * Convert to an entry.
     * @return Bytecode entry.
     */
    XmlBytecodeEntry toEntry() {
        final XmlBytecodeEntry result;
        final Optional<String> base = this.attribute("base");
        if (base.isPresent() && new JeoFqn("label").fqn().equals(base.get())) {
            result = new XmlLabel(this);
        } else if (base.isPresent() && new JeoFqn("frame").fqn().equals(base.get())) {
            result = new XmlFrame(this);
        } else {
            result = new XmlInstruction(this);
        }
        return result;
    }

    /**
     * Convert to XML document.
     * @return XML document.
     */
    XMLDocument asDocument() {
        return new XMLDocument(this.node);
    }

    /**
     * Get optional child node.
     * @param name Child node name.
     * @return Child node.
     */
    private Optional<XmlNode> optchild(final String name) {
        Optional<XmlNode> result = Optional.empty();
        final NodeList children = this.node.getChildNodes();
        for (int index = 0; index < children.getLength(); ++index) {
            final Node current = children.item(index);
            if (current.getNodeName().equals(name)) {
                result = Optional.of(new XmlNode(current));
                break;
            }
        }
        return result;
    }

    /**
     * Generate exception if element not found.
     * @param name Element name.
     * @return Exception.
     */
    private IllegalStateException notFound(final String name) {
        return new IllegalStateException(
            String.format(
                "Can't find %s in '%s'",
                name,
                new XMLDocument(this.node)
            )
        );
    }

    /**
     * Objects.
     * @return Stream of class objects.
     */
    private Stream<Node> objects() {
        final NodeList children = this.node.getChildNodes();
        final List<Node> res = new ArrayList<>(children.getLength());
        for (int index = 0; index < children.getLength(); ++index) {
            final Node child = children.item(index);
            if ("o".equals(child.getNodeName())) {
                res.add(child);
            }
        }
        return res.stream();
    }
}
