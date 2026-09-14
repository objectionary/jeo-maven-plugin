/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link OpcodeDictionary}.
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

    @ParameterizedTest
    @MethodSource("asmOpcodes")
    void mapsEveryAsmOpcodeBackToItself(final int opcode) {
        MatcherAssert.assertThat(
            String.format("Opcode %d must map to its own name and back", opcode),
            new OpcodeDictionary().code(new OpcodeDictionary().name(opcode)),
            Matchers.equalTo(opcode)
        );
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "ldc_w", "ldc2_w",
            "iload_0", "iload_1", "aload_3",
            "istore_0", "astore_3",
            "wide", "goto_w", "jsr_w"
        }
    )
    void rejectsPseudoOpcodesNormalizedByAsm(final String name) {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new OpcodeDictionary().code(name),
            String.format(
                "ASM normalizes the pseudo-opcode '%s' away, so the dictionary must not contain it",
                name
            )
        );
    }

    /**
     * Opcode values exposed by ASM.
     * ASM exposes the real JVM opcodes in the range
     * {@link Opcodes#NOP}..{@link Opcodes#IFNONNULL}, but normalizes the pseudo-opcodes
     * (ldc_w, ldc2_w, iload_n..aload_n, istore_n..astore_n, wide, goto_w, jsr_w) into
     * their compact forms, so they never appear as constants here. The same range also
     * contains non-opcode constants (ACC_*, V1_*, TOP/INTEGER/...), which are filtered
     * out by checking that the constant name matches the opcode name.
     * @return Stream of distinct ASM opcode values.
     */
    private static Stream<Arguments> asmOpcodes() {
        return Arrays.stream(Opcodes.class.getFields())
            .filter(field -> field.getType() == int.class)
            .filter(OpcodeDictionaryTest::isAsmOpcode)
            .map(OpcodeDictionaryTest::value)
            .distinct()
            .map(Arguments::of);
    }

    /**
     * Check that the field is a genuine opcode constant.
     * @param field Field to check.
     * @return True if the field is an opcode constant.
     */
    private static boolean isAsmOpcode(final Field field) {
        final int code = OpcodeDictionaryTest.value(field);
        boolean result = false;
        if (code >= Opcodes.NOP && code <= Opcodes.IFNONNULL) {
            try {
                result = new OpcodeDictionary().name(code)
                    .equals(field.getName().toLowerCase(Locale.ROOT));
            } catch (final IllegalStateException exception) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Value of a static int field.
     * @param field Field to read.
     * @return Field value.
     */
    private static int value(final Field field) {
        try {
            return field.getInt(null);
        } catch (final IllegalAccessException exception) {
            throw new IllegalStateException(
                String.format("Can't read the value of the field '%s'", field),
                exception
            );
        }
    }
}
