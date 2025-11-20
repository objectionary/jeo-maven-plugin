/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeInstruction;
import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlInstruction}.
 * @since 0.1
 */
final class XmlInstructionTest {

    @Test
    void comparesSuccessfullyWithSpaces() {
        MatcherAssert.assertThat(
            "Xml Instruction nodes with different empty spaces, but with the same content should be the same, but it wasn't",
            new XmlInstruction(0, Opcodes.INVOKESPECIAL, 1, 2, 3).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.INVOKESPECIAL, 1, 2, 3))
        );
    }

    @Test
    void comparesSuccessfullyWithDifferentTextNodes() {
        MatcherAssert.assertThat(
            "Xml Instruction with different arguments should not be equal, but it was",
            new XmlInstruction(0, Opcodes.INVOKESPECIAL, 32, 23, 14).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.INVOKESPECIAL, 32, 23, 14))
        );
    }

    @Test
    void comparesDeeply() {
        MatcherAssert.assertThat(
            "Xml Instruction with different child content should not be equal, but it was",
            new XmlInstruction(0, Opcodes.INVOKESPECIAL).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.INVOKESPECIAL))
        );
    }

    @Test
    void comparesDifferentInstructions() {
        MatcherAssert.assertThat(
            "Xml Instruction with different content should not be equal, but it was",
            new XmlInstruction(0, Opcodes.DUP).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.DUP))
        );
    }

    @Test
    void throwsExceptionForInvalidOpcode() {
        MatcherAssert.assertThat(
            "Error message should contain invalid opcode number",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new XmlInstruction(
                    new JcabiXmlNode(
                        new Xembler(new DirectivesInstruction(0, new Format(), Opcodes.DUP))
                            .xml()
                            .replace("dup", "unknown")
                    )
                ).bytecode(),
                "Should throw an exception for invalid opcode"
            ).getMessage(),
            Matchers.containsString(
                "Unknown opcode name: unknown"
            )
        );
    }
}
