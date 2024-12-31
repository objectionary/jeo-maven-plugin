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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.lints.Defect;
import org.eolang.lints.Program;
import org.eolang.lints.Severity;
import org.eolang.parser.StrictXmir;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Jcabi XML node.
 * @since 0.8
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings("PMD.TooManyMethods")
public final class JcabiXmlNode implements XmlNode {

    /**
     * XML document.
     */
    private final XML doc;

    /**
     * Ctor.
     * @param xml XML string.
     */
    JcabiXmlNode(final String... xml) {
        this(new XMLDocument(String.join("\n", xml)).inner().getFirstChild());
    }

    /**
     * Ctor.
     * @param item XML node.
     */
    JcabiXmlNode(final Node item) {
        this(new XMLDocument(item));
    }

    /**
     * Ctor.
     * @param root XML document.
     */
    JcabiXmlNode(final XML root) {
        this.doc = root;
    }

    @Override
    public Stream<XmlNode> children() {
        return this.objects().map(JcabiXmlNode::new);
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
        // @checkstyle MethodBodyCommentsCheck (4 lines)
        // @todo #939:60min Fix All The Warnings in the EO Representation.
        //  Here we just catch only the errors in the EO representation.
        //  We need to fix all the warnings in the EO representation as well.
        final Collection<Defect> defects = new Program(new StrictXmir(this.doc)).defects()
            .stream()
            .filter(defect -> defect.severity() == Severity.ERROR)
            .collect(Collectors.toList());
        if (!defects.isEmpty()) {
            throw new IllegalStateException(
                String.format(
                    "XMIR is incorrect: %s, %n%s%n",
                    defects,
                    this.doc
                )
            );
        }
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

    /**
     * Objects.
     * @return Stream of class objects.
     */
    private Stream<Node> objects() {
        final NodeList children = this.doc.inner().getChildNodes();
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
