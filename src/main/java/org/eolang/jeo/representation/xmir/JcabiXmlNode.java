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
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.parser.StrictXmir;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@ToString
@EqualsAndHashCode
public final class JcabiXmlNode implements XmlNode {

    /**
     * XML document.
     */
    private final XMLDocument doc;

    /**
     * Ctor.
     * @param xml XML string.
     */
    public JcabiXmlNode(final String xml) {
        this(new XMLDocument(xml).inner().getFirstChild());
    }

    /**
     * Ctor.
     * @param item XML node.
     */
    public JcabiXmlNode(final Node item) {
        this(new XMLDocument(item));
    }

    /**
     * Ctor.
     * @param root XML document.
     */
    public JcabiXmlNode(final XMLDocument root) {
        this.doc = root;
    }

    @Override
    public Stream<XmlNode> children() {
        final NodeList nodes = this.doc.inner().getChildNodes();
        final int length = nodes.getLength();
        final List<XmlNode> res = new ArrayList<>(length);
        for (int index = 0; index < length; ++index) {
            res.add(new JcabiXmlNode(nodes.item(index)));
        }
        return res.stream();

    }

    @Override
    public String text() {
        return this.doc.inner().getTextContent();
    }

    @Override
    public Optional<String> attribute(final String name) {
        Optional<String> result = Optional.empty();
        final NamedNodeMap attributes = this.doc.inner().getAttributes();
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

    @Override
    public List<String> xpath(final String xpath) {
        return this.doc.xpath(xpath);
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
                        this.doc
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
        new StrictXmir(this.doc).inner();
    }

    /**
     * Get optional child node.
     * @param name Child node name.
     * @return Child node.
     */
    private Optional<XmlNode> optchild(final String name) {
        Optional<XmlNode> result = Optional.empty();
        final NodeList children = this.doc.inner().getChildNodes();
        final int length = children.getLength();
        for (int index = 0; index < length; ++index) {
            final Node current = children.item(index);
            if (current.getNodeName().equals(name)) {
                result = Optional.of(new JcabiXmlNode(current));
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
                this.doc
            )
        );
    }
}
