/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
    private final XmlJeoObject node;

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
        final Object value = new XmlValue(
            this.node.children().findFirst()
                .orElseThrow(
                    () -> new IllegalStateException(
                        String.format("fist child isn't found in node %s", this.node)
                    )
                )
        ).object();
        if (!(value instanceof Integer)) {
            throw new IllegalArgumentException(
                String.format(
                    "Can't parse opcode number from the node '%s', opcode value is '%s', but the opcode number should be an integer",
                    this.node,
                    value
                )
            );
        }
        return (int) value;
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
