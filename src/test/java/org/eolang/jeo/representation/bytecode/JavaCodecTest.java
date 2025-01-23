/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
package org.eolang.jeo.representation.bytecode;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Type;

/**
 * Test case for {@link JavaCodec}.
 * @since 0.8
 */
final class JavaCodecTest {

    /**
     * Empty byte array.
     */
    private static final byte[] EMPTY = new byte[0];

    @ParameterizedTest
    @MethodSource("mapping")
    void encodesSuccessfully(final Object value, final DataType type, final byte[] bytes) {
        MatcherAssert.assertThat(
            "Can't encode value to the correct byte array",
            new JavaCodec().encode(value, type),
            Matchers.equalTo(bytes)
        );
    }

    /**
     * Test cases.
     * @return Arguments.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> mapping() {
        return Stream.of(
            Arguments.of(null, DataType.NULL, JavaCodecTest.EMPTY),
            Arguments.of(true, DataType.BOOL, new byte[]{1}),
            Arguments.of('a', DataType.CHAR, new byte[]{0, 97}),
            Arguments.of(new byte[]{0, 1, 2, 3}, DataType.BYTES, new byte[]{0, 1, 2, 3}),
            Arguments.of(
                "hello, world!",
                DataType.STRING,
                "hello, world!".getBytes(StandardCharsets.UTF_8)
            ),
            Arguments.of(
                new BytecodeLabel("label"),
                DataType.LABEL,
                "label".getBytes(StandardCharsets.UTF_8)
            ),
            Arguments.of(
                Type.getType(Object.class),
                DataType.TYPE_REFERENCE,
                "Ljava/lang/Object;".getBytes(StandardCharsets.UTF_8)
            ),
            Arguments.of(
                Object.class,
                DataType.CLASS_REFERENCE,
                "java/lang/Object".getBytes(StandardCharsets.UTF_8)
            )
        );
    }
}
