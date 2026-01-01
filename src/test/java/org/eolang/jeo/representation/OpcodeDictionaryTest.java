/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link OpcodeDictionary}.
 * @since 0.12.0
 */
final class OpcodeDictionaryTest {

    @Test
    void retrievesNameForValidOpcode() {
        MatcherAssert.assertThat(
            "We expect that the opcode name for NOP is 'nop'",
            new OpcodeDictionary().name(org.objectweb.asm.Opcodes.NOP),
            Matchers.is(Matchers.equalTo("nop"))
        );
    }

    @Test
    void retrievesUnknownForInvalidOpcode() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new OpcodeDictionary().name(-1),
            "Expected an exception for an invalid opcode"
        );
    }

    @Test
    void retrievesOpcodeForValidName() {
        MatcherAssert.assertThat(
            "We expect that the opcode for 'nop' is correct",
            new OpcodeDictionary().code("nop"),
            Matchers.is(Matchers.equalTo(org.objectweb.asm.Opcodes.NOP))
        );
    }

    @Test
    void retrievesDefaultOpcodeForInvalidName() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new OpcodeDictionary().code("invalid-name"),
            "Expected an exception for an invalid opcode name"
        );
    }

    @Test
    void handlesCaseInsensitiveOpcodeNames() {
        MatcherAssert.assertThat(
            "We expect that the opcode name is case-insensitive",
            new OpcodeDictionary().code("NOP"),
            Matchers.is(Matchers.equalTo(org.objectweb.asm.Opcodes.NOP))
        );
    }
}
