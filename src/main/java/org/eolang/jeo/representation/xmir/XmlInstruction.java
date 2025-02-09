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

import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.bytecode.BytecodeInstruction;
import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Bytecode instruction from XML.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class XmlInstruction implements XmlBytecodeEntry {

    /**
     * Instruction node.
     */
    @EqualsAndHashCode.Exclude
    private final XmlNode node;

    /**
     * Constructor.
     * @param opcode Opcode.
     * @param args Arguments.
     */
    XmlInstruction(final int opcode, final Object... args) {
        this(
            new NativeXmlNode(
                new Xembler(
                    new DirectivesInstruction(opcode, args),
                    new Transformers.Node()
                ).xmlQuietly()
            )
        );
    }

    /**
     * Constructor.
     * @param xmlnode Instruction node.
     */
    XmlInstruction(final XmlNode xmlnode) {
        this.node = xmlnode;
    }

    /**
     * Convert to bytecode.
     * @return Bytecode instruction.
     */
    public BytecodeInstruction bytecode() {
        return new BytecodeInstruction(
            this.opcode(),
            this.operands().stream().map(XmlOperand::asObject).toArray()
        );
    }

    /**
     * Instruction code.
     * @return Code.
     */
    @EqualsAndHashCode.Include
    private int opcode() {
        return (int) new XmlValue(this.node.firstChild()).object();
    }

    /**
     * Instruction arguments.
     * @return Arguments.
     */
    @EqualsAndHashCode.Include
    private List<XmlOperand> operands() {
        return this.node
            .children()
            .skip(1)
            .map(XmlOperand::new)
            .collect(Collectors.toList());
    }
}
