/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.lang.reflect.Field;
import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Printer;

/**
 * Test case for {@link OpcodeDictionary}.
 *
 * @since 0.12.0
 */
final class OpcodeDictionaryTest {

    @Test
    void retrievesNameForValidOpcode() {
        MatcherAssert.assertThat(
            "We expect that the opcode name for NOP is 'nop'",
            new OpcodeDictionary().name(Opcodes.NOP),
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
            Matchers.is(Matchers.equalTo(Opcodes.NOP))
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
            Matchers.is(Matchers.equalTo(Opcodes.NOP))
        );
    }

    @Test
    void mapsTheInstructionConstantsExposedByAsm() throws IllegalAccessException {
        final OpcodeDictionary dictionary = new OpcodeDictionary();
        for (final Field field : Opcodes.class.getFields()) {
            if (field.getType().equals(Integer.TYPE)) {
                final int code = field.getInt(null);
                if (code >= 0 && code < Printer.OPCODES.length
                    && field.getName().equals(Printer.OPCODES[code])) {
                    final String name = field.getName().toLowerCase(Locale.ROOT);
                    Assertions.assertArrayEquals(
                        new Object[]{name, code},
                        new Object[]{dictionary.name(code), dictionary.code(name)},
                        name
                    );
                }
            }
        }
    }
}
