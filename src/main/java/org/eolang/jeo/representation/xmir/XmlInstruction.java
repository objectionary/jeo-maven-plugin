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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Bytecode instruction from XML.
 * @since 0.1
 */
public final class XmlInstruction {

    /**
     * Instruction node.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    private final Node node;

    /**
     * Constructor.
     * @param node Instruction node.
     */
    public XmlInstruction(final Node node) {
        this.node = node;
    }

    /**
     * Instruction code.
     * @return Code.
     */
    public int code() {
        return Integer.parseInt(
            this.node.getAttributes()
                .getNamedItem("name")
                .getNodeValue().split("-")[1]
        );
    }

    /**
     * Instruction arguments.
     * @return Arguments.
     */
    public Object[] arguments() {
        return XmlInstruction.arguments(this.node);
    }

    /**
     * XML node.
     * @return XML node.
     * @todo #157:90min Hide internal node representation in XmlInstruction.
     *  This class should not expose internal node representation.
     *  We have to consider to add methods or classes in order to avoid
     *  exposing internal node representation.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public Node node() {
        return this.node;
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other == null || this.getClass() != other.getClass()) {
            result = false;
        } else {
            final XmlInstruction that = (XmlInstruction) other;
            result = this.equals(this.node, that.node);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.node);
    }

    @Override
    public String toString() {
        return new XMLDocument(this.node).toString();
    }

    /**
     * Get opcode arguments.
     * @param node Node.
     * @return Arguments.
     */
    private static Object[] arguments(final Node node) {
        final NodeList children = node.getChildNodes();
        final Collection<Object> res = new ArrayList<>(children.getLength());
        for (int index = 0; index < children.getLength(); ++index) {
            final Node child = children.item(index);
            if (child.getNodeName().equals("o")) {
                final NamedNodeMap attributes = child.getAttributes();
                if (attributes.getNamedItem("base").getNodeValue().equals("int")) {
                    res.add(new HexString(child.getTextContent()).decodeAsInt());
                } else {
                    res.add(new HexString(child.getTextContent()).decode());
                }
            }
        }
        return res.toArray();
    }

    /**
     * Check if two nodes are equal.
     * @param first First node.
     * @param second Second node.
     * @return True if nodes are equal.
     */
    private boolean equals(final Node first, final Node second) {
        final boolean result;
        if (first.getNodeType() != second.getNodeType()) {
            result = false;
        } else if (!first.getNodeName().equals(second.getNodeName())) {
            result = false;
        } else if (first.getNodeType() == Node.TEXT_NODE && !first.getTextContent()
            .trim().equals(second.getTextContent().trim())) {
            result = false;
        } else if (first.getAttributes().getLength() != second.getAttributes().getLength()) {
            result = false;
        } else if (!XmlInstruction.sameAttributes(first, second)) {
            result = false;
        } else {
            result = this.sameChildren(first, second);
        }
        return result;
    }

    private static boolean sameAttributes(final Node first, final Node second) {
        boolean result = true;
        for (int index = 0; index < first.getAttributes().getLength(); ++index) {
            final Node attr1 = first.getAttributes().item(index);
            final Node attr2 = second.getAttributes().getNamedItem(attr1.getNodeName());
            if (attr1.getNodeName().equals("name")) {
                if (attr1.getNodeValue().split("-")[0]
                    .equals(attr2.getNodeValue().split("-")[0])) {
                    continue;
                }
            }
            if (attr2 == null || !attr1.getNodeValue().equals(attr2.getNodeValue())) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean sameChildren(final Node first, final Node second) {
        boolean result = true;
        final List<Node> firstchildren = XmlInstruction.children(first);
        final List<Node> secondchildren = XmlInstruction.children(second);
        if (firstchildren.size() != secondchildren.size()) {
            result = false;
        } else {
            for (int i = 0; i < firstchildren.size(); ++i) {
                if (!this.equals(firstchildren.get(i), secondchildren.get(i))) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Get children nodes.
     * @param root Root node.
     * @return Children nodes.
     */
    private static List<Node> children(final Node root) {
        final NodeList all = root.getChildNodes();
        final List<Node> res = new ArrayList<>(0);
        for (int index = 0; index < all.getLength(); ++index) {
            final Node item = all.item(index);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                res.add(item);
            }
        }
        return res;
    }
}
