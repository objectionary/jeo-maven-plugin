/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XMLDocument;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.eolang.parser.StrictXmir;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML smart element.
 * Utility class that simplifies work with XML.
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class NativeXmlNode implements XmlNode {

    /**
     * XPath's factory.
     */
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

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
        final XPath path = NativeXmlNode.XPATH_FACTORY.newXPath();
        try {
            final NodeList all = (NodeList) path.evaluate(
                xpath,
                this.node,
                XPathConstants.NODESET
            );
            final int length = all.getLength();
            final List<String> res = new ArrayList<>(0);
            for (int index = 0; index < length; ++index) {
                final Node item = all.item(index);
                res.add(item.getNodeValue());
            }
            return Collections.unmodifiableList(res);
        } catch (final XPathExpressionException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't evaluate xpath '%s' in '%s'",
                    xpath,
                    this.node
                ),
                exception
            );
        }
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

    @Override
    public void validate() {
        new StrictXmir(new XMLDocument(new XMLDocument(this.node).toString())).inner();
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
