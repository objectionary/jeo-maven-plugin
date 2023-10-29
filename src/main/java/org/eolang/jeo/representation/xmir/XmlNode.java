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

import com.jcabi.xml.XMLDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML smart element.
 * Utility class that simplifies work with XML.
 * @since 0.1
 * @todo #193:90min Add unit tests for XmlNode.
 *  Currently we don't have unit tests for XmlNode. So, it makes sense to add
 *  them to keep code safe and clear.
 */
final class XmlNode {

    /**
     * Parent node.
     */
    private final Node node;

    /**
     * Constructor.
     * @param parent Parent node.
     */
    XmlNode(final Node parent) {
        this.node = parent;
    }

    /**
     * Get child node.
     * @param name Child node name.
     * @return Child node.
     */
    XmlNode child(final String name) {
        final NodeList children = this.node.getChildNodes();
        for (int index = 0; index < children.getLength(); ++index) {
            final Node current = children.item(index);
            if (current.getNodeName().equals(name)) {
                return new XmlNode(current);
            }
        }
        throw this.notFound(name);
    }

    /**
     * Get child node by attribute
     */
    XmlNode child(final String attribute, final String value) {
        return this.children()
            .map(child -> child.attribute(attribute))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(child -> child.equals(value))
            .findFirst()
            .orElseThrow(() -> this.notFound(String.format("%s:%s", attribute, value)));
    }

    /**
     * Get all child nodes.
     * @return Child nodes.
     */
    Stream<XmlNode> children() {
        return this.objects().map(XmlNode::new);
    }

    /**
     * Convert to class.
     * @return Class.
     */
    XmlClass toClass() {
        return new XmlClass(this.node);
    }

    /**
     * Convert to an instruction.
     * @return Instruction.
     */
    XmlInstruction toInstruction() {
        return new XmlInstruction(this.node);
    }

    /**
     * Retrieve node text content.
     * @return Text content.
     */
    String text() {
        return this.node.getTextContent();
    }

    /**
     * Set node text content.
     * @param text Text content.
     */
    void withText(final String text) {
        this.node.setTextContent(text);
    }

    /**
     * Set node attribute.
     * @param name Attribute name.
     * @param value Attribute value.
     */
    void withAttribute(final String name, final String value) {
        final NamedNodeMap attributes = this.node.getAttributes();
        if (null == attributes.getNamedItem(name)) {
            attributes.setNamedItem(this.node.getOwnerDocument().createAttribute(name));
        }
        attributes.getNamedItem(name).setNodeValue(value);
        this.node.getAttributes().getNamedItem(name).setNodeValue(value);
    }

    /**
     * Clean all child nodes.
     * @return The same node.
     */
    XmlNode clean() {
        while (this.node.hasChildNodes()) {
            this.node.removeChild(this.node.getFirstChild());
        }
        return this;
    }

    /**
     * Append child node.
     * @param child Node to append.
     * @return The same node.
     */
    XmlNode append(final Node child) {
        this.node.appendChild(this.node.getOwnerDocument().adoptNode(child.cloneNode(true)));
        return this;
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
     * Get attribute.
     * @param name Attribute name.
     * @return Attribute.
     */
    Optional<Object> attribute(final String name) {
        final Optional<Object> result;
        final NamedNodeMap attrs = this.node.getAttributes();
        if (attrs == null) {
            result = Optional.empty();
        } else {
            result = Optional.ofNullable(attrs.getNamedItem(name));
        }
        return result;
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
            if (child.getNodeName().equals("o")) {
                res.add(child);
            }
        }
        return res.stream();
    }
}
