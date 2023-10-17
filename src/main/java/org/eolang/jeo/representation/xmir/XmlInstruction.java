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

import java.util.ArrayList;
import java.util.Collection;
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
}
