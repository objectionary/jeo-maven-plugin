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

import java.util.Arrays;
import org.eolang.jeo.representation.bytecode.BytecodeEntry;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.objectweb.asm.MethodVisitor;

/**
 * Xmir representation of bytecode frame.
 *
 * @since 0.3
 */
public final class XmlFrame implements XmlBytecodeEntry {

    /**
     * Object attribute name.
     */
    private static final String NAME_ATTR = "name";

    /**
     * Xmir node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param lines Lines of XML
     */
    public XmlFrame(final String... lines) {
        this(String.join("\n", lines));
    }

    /**
     * Constructor.
     * @param xml XML
     */
    public XmlFrame(final String xml) {
        this(new XmlNode(xml));
    }

    /**
     * Constructor.
     * @param node Xmir node
     */
    public XmlFrame(final XmlNode node) {
        this.node = node;
    }

    @Override
    public void writeTo(final BytecodeMethod method) {
        method.entry(new FrameEntry());
    }

    /**
     * Type of frame.
     * @return Type.
     */
    int type() {
        return (int) new XmlOperand(this.node.child(XmlFrame.NAME_ATTR, "type")).asObject();
    }

    /**
     * Number of local variables.
     * @return Number of local variables.
     */
    int nlocal() {
        return (int) new XmlOperand(this.node.child(XmlFrame.NAME_ATTR, "nlocal")).asObject();
    }

    /**
     * Number of stack elements.
     * @return Number of stack elements.
     */
    int nstack() {
        return (int) new XmlOperand(this.node.child(XmlFrame.NAME_ATTR, "nstack")).asObject();
    }

    /**
     * Local variables.
     * @return Local variables.
     */
    Object[] locals() {
        return this.node.child(XmlFrame.NAME_ATTR, "local")
            .children()
            .map(XmlOperand::new)
            .map(XmlOperand::asObject)
            .toArray(Object[]::new);
    }

    /**
     * Stack elements.
     * @return Stack elements.
     */
    Object[] stack() {
        return this.node.child(XmlFrame.NAME_ATTR, "stack")
            .children()
            .map(XmlOperand::new)
            .map(XmlOperand::asObject)
            .toArray(Object[]::new);
    }

    /**
     * Frame entry.
     *
     * @since 0.3
     */
    private final class FrameEntry implements BytecodeEntry {
        @Override
        public void writeTo(final MethodVisitor visitor) {
            visitor.visitFrame(
                XmlFrame.this.type(),
                XmlFrame.this.nlocal(),
                XmlFrame.this.locals(),
                XmlFrame.this.nstack(),
                XmlFrame.this.stack()
            );
        }

        @Override
        public String testCode() {
            return String.format(
                ".visitFrame(%d, %d, new Object[]{ %s }, %d, new Object[]{ %s })",
                XmlFrame.this.type(),
                XmlFrame.this.nlocal(),
                Arrays.toString(XmlFrame.this.locals()),
                XmlFrame.this.nstack(),
                Arrays.toString(XmlFrame.this.stack())
            );
        }
    }
}
