/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Opcodes;

/**
 * Test cases for {@link OpcodeName}.
 * This class verifies the correct mapping of JVM opcodes to their string representations,
 * ensuring accurate bytecode instruction naming.
 *
 * @since 0.1.0
 */
final class OpcodeNameTest {

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("opcodes")
    void checksOpcodeNames(final AtomicInteger counter, final int actual, final String expected) {
        MatcherAssert.assertThat(
            "Opcode name is not as expected",
            new OpcodeName(actual, counter).asString(),
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
        final AtomicInteger counter = new AtomicInteger(0);
        return Stream.of(
            Arguments.of(counter, Opcodes.INVOKESPECIAL, "invokespecial-1"),
            Arguments.of(counter, Opcodes.INVOKEVIRTUAL, "invokevirtual-2"),
            Arguments.of(counter, Opcodes.INVOKESTATIC, "invokestatic-3"),
            Arguments.of(counter, Opcodes.INVOKEINTERFACE, "invokeinterface-4"),
            Arguments.of(counter, Opcodes.INVOKEDYNAMIC, "invokedynamic-5"),
            Arguments.of(counter, Opcodes.DUP, "dup-6"),
            Arguments.of(counter, Opcodes.LDC, "ldc-7"),
            Arguments.of(counter, Opcodes.ALOAD, "aload-8"),
            Arguments.of(counter, Opcodes.ASTORE, "astore-9"),
            Arguments.of(counter, Opcodes.ILOAD, "iload-A"),
            Arguments.of(counter, Opcodes.ISTORE, "istore-B")
        );
    }
}
