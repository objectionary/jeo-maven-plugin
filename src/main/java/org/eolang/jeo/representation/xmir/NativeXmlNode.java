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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML smart element.
 * Utility class that simplifies work with XML.
 * @since 0.1
 */
public final class NativeXmlNode implements XmlNode {

    /**
     * XML node.
     * Attention!
     * Here we use the {@link Node} class instead of the {@link com.jcabi.xml.XML}
     * by performance reasons.
     * In some cases {@link Node} 10 times faster than {@link com.jcabi.xml.XML}.
     * You can read more about it here:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/pull/924">Optimization</a>
     */
    private final Node node;

    /**
     * Constructor.
     * @param xml XML string.
     */
    public NativeXmlNode(final String xml) {
        this(new XMLDocument(xml).inner().getFirstChild());
    }

    /**
     * Constructor.
     * @param parent Xml node.
     */
    public NativeXmlNode(final Node parent) {
        this.node = parent;
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean res;
        if (obj instanceof NativeXmlNode) {
            res = new XMLDocument(this.node).equals(new XMLDocument(((NativeXmlNode) obj).node));
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
        return this.node.toString();
    }


    @Override
    public Stream<XmlNode> children() {
        return this.objects().map(NativeXmlNode::new);
    }


    @Override
    public String text() {
        return this.node.getTextContent();
    }

    @Override
    public Optional<String> attribute(final String name) {
        Optional<String> result = Optional.empty();
        final NamedNodeMap attributes = this.node.getAttributes();
        final int length = attributes.getLength();
        for (int index = 0; index < length; ++index) {
            final Node item = attributes.item(index);
            if (item.getNodeName().startsWith(name)) {
                result = Optional.of(item.getTextContent());
                break;
            }
        }
        return result;
    }

    @Override
    public XmlNode child(final String name) {
        return this.optchild(name).orElseThrow(() -> this.notFound(name));
    }

    /**
     * Find elements by xpath.
     * @param xpath XPath.
     * @return List of elements.
     */
    public List<String> xpath(final String xpath) {
        return new XMLDocument(this.node).xpath(xpath);
    }

    @Override
    public XmlNode child(final String attribute, final String value) {
        return this.children()
            .filter(xmlnode -> xmlnode.hasAttribute(attribute, value))
            .findFirst()
            .orElseThrow(
                () -> this.notFound(
                    String.format("object with attribute %s='%s'", attribute, value)
                )
            );
    }

    @Override
    public Optional<XmlNode> optchild(final String attribute, final String value) {
        return this.children()
            .filter(xmlnode -> xmlnode.hasAttribute(attribute, value))
            .findFirst();
    }

    @Override
    public XmlNode firstChild() {
        return this.children().findFirst()
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "Can't find any child nodes in '%s'",
                        this.node
                    )
                )
            );
    }

    @Override
    public boolean hasAttribute(final String name, final String value) {
        return this.attribute(name)
            .map(String::valueOf)
            .map(val -> val.startsWith(value))
            .orElse(false);
    }


    /**
     * Get optional child node.
     * @param name Child node name.
     * @return Child node.
     */
    private Optional<XmlNode> optchild(final String name) {
        Optional<XmlNode> result = Optional.empty();
        final NodeList children = this.node.getChildNodes();
        final int length = children.getLength();
        for (int index = 0; index < length; ++index) {
            final Node current = children.item(index);
            if (current.getNodeName().equals(name)) {
                result = Optional.of(new NativeXmlNode(current));
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
                this.node
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
