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
package org.eolang.jeo.representation.asm;

import java.util.ArrayList;
import java.util.Collection;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Bytecode instruction from XML.
 * @since 0.1
 */
final class XmlInstruction {

    /**
     * Instruction node.
     */
    private final Node node;

    /**
     * Constructor.
     * @param node Instruction node.
     */
    XmlInstruction(final Node node) {
        this.node = node;
    }

    /**
     * Instruction code.
     * @return Code.
     */
    int code() {
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
    Object[] arguments() {
        return XmlInstruction.arguments(this.node);
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
                res.add(XmlData.encodeHexString(child.getTextContent()));
            }
        }
        return res.toArray();
    }
}
