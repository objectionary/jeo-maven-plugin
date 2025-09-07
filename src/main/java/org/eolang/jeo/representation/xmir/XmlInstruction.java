/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.OpcodeDictionary;
import org.eolang.jeo.representation.bytecode.BytecodeInstruction;
import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.eolang.jeo.representation.directives.Format;
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
     * Opcode dictionary.
     */
    private static final OpcodeDictionary DICTIONARY = new OpcodeDictionary();

    /**
     * Instruction node.
     */
    @EqualsAndHashCode.Exclude
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param index Index of the instruction among other instructions.
     * @param opcode Opcode.
     * @param args Arguments.
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
     * @param xmlnode Instruction node.
     */
    XmlInstruction(final XmlNode xmlnode) {
        this(new XmlJeoObject(xmlnode));
    }

    /**
     * Constructor.
     * @param node XML Jeo object node representing the instruction.
     */
    private XmlInstruction(final XmlJeoObject node) {
        this.node = node;
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
        return this.node.base()
            .map(s -> s.substring(s.lastIndexOf('.') + 1))
            .map(XmlInstruction.DICTIONARY::code)
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format("base is not found in node %s", this.node)
                )
            );
    }

    /**
     * Instruction arguments.
     * @return Arguments.
     */
    @EqualsAndHashCode.Include
    private List<XmlOperand> operands() {
        return this.node
            .children()
            .map(XmlOperand::new)
            .collect(Collectors.toList());
    }
}
