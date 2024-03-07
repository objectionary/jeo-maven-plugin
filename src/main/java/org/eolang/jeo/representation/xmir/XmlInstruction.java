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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.w3c.dom.Node;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Bytecode instruction from XML.
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class XmlInstruction implements XmlBytecodeEntry {

    /**
     * Instruction node.
     */
    private final Node node;

    /**
     * Constructor.
     * @param opcode Opcode.
     * @param args Arguments.
     */
    public XmlInstruction(final int opcode, final Object... args) {
        this(true, opcode, args);
    }

    /**
     * Constructor.
     * @param opcode Opcode.
     * @param args Arguments.
     */
    public XmlInstruction(final boolean counting, final int opcode, final Object... args) {
        this(
            new XMLDocument(
                new Xembler(
                    new DirectivesInstruction(opcode, counting, args),
                    new Transformers.Node()
                ).xmlQuietly()
            ).node().getFirstChild()
        );
    }

    /**
     * Constructor.
     * @param node Instruction node.
     */
    public XmlInstruction(final Node node) {
        this.node = node;

    }

    @Override
    public void writeTo(final BytecodeMethod method) {
        method.opcode(this.opcode(), this.operands().stream().map(XmlOperand::asObject).toArray());
    }

    /**
     * Instruction code.
     * @return Code.
     */
    public int opcode() {
        return new HexString(new XmlNode(this.node).firstChild().text()).decodeAsInt();
    }

    /**
     * Instruction arguments.
     * @return Arguments.
     */
    public List<XmlOperand> operands() {
        return new XmlNode(this.node)
            .children()
            .skip(1)
            .map(XmlOperand::new)
            .collect(Collectors.toList());
    }

    /**
     * Get XML node.
     * @return XML node.
     */
    public XmlNode toNode() {
        return new XmlNode(this.node);
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other == null || this.getClass() != other.getClass()) {
            result = false;
        } else {
            result = this.equals(this.node, ((XmlInstruction) other).node);
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
     * Check if two nodes are equal.
     * @param first First node.
     * @param second Second node.
     * @return True if nodes are equal.
     */
    private boolean equals(final Node first, final Node second) {
        return new XmlNode(first).equals(new XmlNode(second));
    }
}
