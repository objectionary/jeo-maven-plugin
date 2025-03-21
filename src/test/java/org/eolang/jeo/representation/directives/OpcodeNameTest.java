/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link OpcodeName}.
 * @since 0.1
 */
final class OpcodeNameTest {

    /**
     * Opcode counter.
     */
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("opcodes")
    void checksOpcodeNames(final int actual, final String expected) {
        MatcherAssert.assertThat(
            "Opcode name is not as expected",
            new OpcodeName(actual, OpcodeNameTest.COUNTER).asString(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void returnsOpcode() {
        final int expected = 0;
        final int code = new OpcodeName(expected, OpcodeNameTest.COUNTER)
            .code();
        MatcherAssert.assertThat(
            String.format(
                "Opcode code: %d does not matches with expected: %d",
                code,
                expected
            ),
            code,
            Matchers.equalTo(expected)
        );
    }

    /**
     * Provides test actual and expected arguments.
     * PMD argues that this method is unused, but it is used by JUnit.
     * So we just suppress this warning.
     * @return Stream of arguments.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> opcodes() {
        return Stream.of(
            Arguments.of(Opcodes.INVOKESPECIAL, "invokespecial-1"),
            Arguments.of(Opcodes.INVOKEVIRTUAL, "invokevirtual-2"),
            Arguments.of(Opcodes.INVOKESTATIC, "invokestatic-3"),
            Arguments.of(Opcodes.INVOKEINTERFACE, "invokeinterface-4"),
            Arguments.of(Opcodes.INVOKEDYNAMIC, "invokedynamic-5"),
            Arguments.of(Opcodes.DUP, "dup-6"),
            Arguments.of(Opcodes.LDC, "ldc-7"),
            Arguments.of(Opcodes.ALOAD, "aload-8"),
            Arguments.of(Opcodes.ASTORE, "astore-9"),
            Arguments.of(Opcodes.ILOAD, "iload-A"),
            Arguments.of(Opcodes.ISTORE, "istore-B")
        );
    }
}
