/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
