/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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

import java.util.Objects;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeFrame;

/**
 * Xmir representation of bytecode frame.
 *
 * @since 0.3
 */
public final class XmlFrame implements XmlBytecodeEntry {

    /**
     * Xmir node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param xmlnode Xmir node
     */
    public XmlFrame(final XmlNode xmlnode) {
        this.node = xmlnode;
    }

    /**
     * Constructor.
     * @param lines Lines of XML
     */
    XmlFrame(final String... lines) {
        this(String.join("\n", lines));
    }

    /**
     * Constructor.
     * @param xml XML
     */
    private XmlFrame(final String xml) {
        this(new NativeXmlNode(xml));
    }

    /**
     * Convert to bytecode.
     * @return Bytecode frame.
     */
    public BytecodeFrame bytecode() {
        return new BytecodeFrame(
            this.type(),
            this.nlocal(),
            this.locals(),
            this.nstack(),
            this.stack()
        );
    }

    /**
     * Type of frame.
     * @return Type.
     */
    private int type() {
        return this.ichild(0);
    }

    /**
     * Number of local variables.
     * @return Number of local variables.
     */
    private int nlocal() {
        return this.ichild(1);
    }

    /**
     * Local variables.
     * @return Local variables.
     */
    private Object[] locals() {
        return this.node.children().collect(Collectors.toList()).get(2)
            .children()
            .map(XmlOperand::new)
            .map(XmlOperand::asObject)
            .toArray(Object[]::new);
    }

    /**
     * Number of stack elements.
     * @return Number of stack elements.
     */
    private int nstack() {
        return this.ichild(3);
    }

    /**
     * Retrieve integer child.
     * @param position Position.
     * @return Integer value.
     */
    private int ichild(final int position) {
        return (int) Objects.requireNonNull(
            new XmlOperand(
                this.node.children().collect(Collectors.toList()).get(position)
            ).asObject(),
            String.format("Can't find integer child at position %d in '%s'", position, this.node)
        );
    }

    /**
     * Stack elements.
     * @return Stack elements.
     */
    private Object[] stack() {
        return this.node.children().collect(Collectors.toList()).get(4)
            .children()
            .map(XmlOperand::new)
            .map(XmlOperand::asObject)
            .toArray(Object[]::new);
    }
}
