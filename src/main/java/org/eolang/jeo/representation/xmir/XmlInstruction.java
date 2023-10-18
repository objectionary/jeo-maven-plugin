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

import com.jcabi.log.Logger;
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

    private boolean nodesAreEqual(Node first, Node second) {
        boolean result = true;
        if (first.getNodeType() != second.getNodeType()) {
            result = false;
        } else if (!first.getNodeName().equals(second.getNodeName())) {
            result = false;
        } else if (!first.getTextContent().trim().equals(second.getTextContent().trim())) {
            result = false;
        } else if (first.getAttributes().getLength() != second.getAttributes().getLength()) {
            result = false;
        } else {
            for (int i = 0; i < first.getAttributes().getLength(); i++) {
                Node attr1 = first.getAttributes().item(i);
                Node attr2 = second.getAttributes().getNamedItem(attr1.getNodeName());
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
            if (result) {
                final List<Node> firstChildren = XmlInstruction.children(first);
                final List<Node> secondChildren = XmlInstruction.children(second);
                if (firstChildren.size() != secondChildren.size()) {
                    result = false;
                } else {
                    for (int i = 0; i < firstChildren.size(); i++) {
                        if (!this.nodesAreEqual(firstChildren.get(i), secondChildren.get(i))) {
                            result = false;
                            break;
                        }
                    }
                }
            }
        }
        Logger.info(
            this,
            String.format(
                "COMPARE %n%s%n VS %n%s%n RESULT: %s",
                new XMLDocument(first),
                new XMLDocument(second),
                result
            )
        );
        return result;
    }

    private static List<Node> children(final Node root) {
        final NodeList all = root.getChildNodes();
        final List<Node> res = new ArrayList<>();
        for (int index = 0; index < all.getLength(); ++index) {
            final Node item = all.item(index);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                res.add(item);
            }
        }
        return res;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        final XmlInstruction that = (XmlInstruction) other;
        return this.nodesAreEqual(this.node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.node);
    }

    @Override
    public String toString() {
        return new XMLDocument(this.node).toString();
    }
}
