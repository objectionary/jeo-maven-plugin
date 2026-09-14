/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.OpcodeDictionary;
import org.eolang.jeo.representation.bytecode.BytecodeInstruction;
import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.eolang.jeo.representation.directives.Format;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Bytecode instruction from XML.
 *
 * @since 0.1
 */
public final class XmlInstruction implements XmlBytecodeEntry {

    /**
     * Opcode dictionary.
     */
    private static final OpcodeDictionary DICTIONARY = new OpcodeDictionary();

    /**
     * Instruction node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param index Index of the instruction among other instructions
     * @param opcode Opcode
     * @param args Arguments
     */
    XmlInstruction(final int index, final int opcode, final Object... args) {
        this(
            new NativeXmlNode(
                new Xembler(
                    new DirectivesInstruction(index, new Format(), opcode, args),
                    new Transformers.Node()
                ).xmlQuietly()
            )
        );
    }

    /**
     * Constructor.
     *
     * @param xmlnode Instruction node
     */
    XmlInstruction(final XmlNode xmlnode) {
        this(new XmlJeoObject(xmlnode));
    }

    /**
     * Constructor.
     *
     * @param node XML Jeo object node representing the instruction
     */
    private XmlInstruction(final XmlJeoObject node) {
        this.node = node;
    }

    @Override
    public BytecodeInstruction bytecode() {
        return new BytecodeInstruction(
            this.opcode(),
            this.operands().stream().map(XmlOperand::asObject).toArray()
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof XmlInstruction) {
            final XmlInstruction instruction = (XmlInstruction) other;
            result = this.opcode() == instruction.opcode()
                && Objects.equals(this.operands(), instruction.operands());
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.opcode(), this.operands());
    }

    @Override
    public String toString() {
        return String.format("XmlInstruction(node=%s)", this.node);
    }

    private int opcode() {
        return this.node.base()
            .map(s -> s.substring(s.lastIndexOf('.') + 1))
            .map(XmlInstruction.DICTIONARY::code).orElseThrow(
                () -> new IllegalStateException(
                    String.format("base is not found in node %s", this.node)
                )
            );
    }

    private List<XmlOperand> operands() {
        return this.node
            .children()
            .map(XmlOperand::new)
            .collect(Collectors.toList());
    }
}
