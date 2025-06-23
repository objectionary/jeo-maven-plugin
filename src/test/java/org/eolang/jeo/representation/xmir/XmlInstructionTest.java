/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeInstruction;
import org.eolang.jeo.representation.directives.DirectivesInstruction;
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

    /**
     * Default instruction which we use for testing.
     */
    private static final BytecodeInstruction EXPECTED = new BytecodeInstruction(
        Opcodes.INVOKESPECIAL, 1, 2, 3
    );

    @Test
    void comparesSuccessfullyWithSpaces() {
        MatcherAssert.assertThat(
            "Xml Instruction nodes with different empty spaces, but with the same content should be the same, but it wasn't",
            new XmlInstruction(Opcodes.INVOKESPECIAL, 1, 2, 3).bytecode(),
            Matchers.equalTo(XmlInstructionTest.EXPECTED)
        );
    }

    @Test
    void comparesSuccessfullyWithDifferentTextNodes() {
        MatcherAssert.assertThat(
            "Xml Instruction with different arguments should not be equal, but it was",
            new XmlInstruction(Opcodes.INVOKESPECIAL, 32, 23, 14),
            Matchers.not(Matchers.equalTo(XmlInstructionTest.EXPECTED))
        );
    }

    @Test
    void comparesDeeply() {
        MatcherAssert.assertThat(
            "Xml Instruction with different child content should not be equal, but it was",
            new XmlInstruction(Opcodes.INVOKESPECIAL),
            Matchers.not(Matchers.equalTo(XmlInstructionTest.EXPECTED))
        );
    }

    @Test
    void comparesDifferentInstructions() {
        MatcherAssert.assertThat(
            "Xml Instruction with different content should not be equal, but it was",
            new XmlInstruction(Opcodes.DUP),
            Matchers.not(Matchers.equalTo(XmlInstructionTest.EXPECTED))
        );
    }

    @Test
    void throwsExceptionForInvalidOpcode() {
        MatcherAssert.assertThat(
            "Error message should contain invalid opcode number",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new XmlInstruction(
                    new JcabiXmlNode(
                        new Xembler(new DirectivesInstruction(Opcodes.DUP))
                            .xml()
                            .replace(".number", ".string")
                    )
                ).bytecode(),
                "Should throw IllegalArgumentException for invalid opcode"
            ).getMessage(),
            Matchers.containsString(
                "opcode value is '@V@\u0000\u0000\u0000\u0000\u0000', but the opcode number should be an integer"
            )
        );
    }
}
