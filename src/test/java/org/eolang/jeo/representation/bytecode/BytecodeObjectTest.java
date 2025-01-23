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
 * Test case for {@link BytecodeObject}.
 * @since 0.6
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class BytecodeObjectTest {

//    @ParameterizedTest
//    @MethodSource("arguments")
//    void convertsToBytes(
//        final BytecodeObject value,
//        final String type,
//        final byte[] bytes,
//        final Object object
//    ) {
//        MatcherAssert.assertThat(
//            "We expect that value will be converted to bytes correctly",
//            value.bytes(),
//            Matchers.equalTo(bytes)
//        );
//    }

    @ParameterizedTest
    @MethodSource("arguments")
    void detectsType(
        final BytecodeObject value,
        final String type,
        final byte[] bytes,
        final Object object
    ) {
        MatcherAssert.assertThat(
            "We expect that type will be detected correctly",
            value.type(),
            Matchers.equalTo(type)
        );
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void retrievesObject(
        final BytecodeObject value,
        final String type,
        final byte[] bytes,
        final Object object
    ) {
        MatcherAssert.assertThat(
            "We expect that object will be retrieved from the value",
            value.value(),
            Matchers.equalTo(object)
        );
    }

    /**
     * Arguments for the tests.
     * Used in these tests:
     * @return Arguments.
     */
    static Stream<Arguments> arguments() {
        return Stream.of(
            Arguments.of(
                new BytecodeObject(42),
                "int",
                new byte[]{0, 0, 0, 0, 0, 0, 0, 42},
                42
            ),
            Arguments.of(
                new BytecodeObject(42L),
                "long",
                new byte[]{0, 0, 0, 0, 0, 0, 0, 42},
                42L
            ),
            Arguments.of(
                new BytecodeObject(42.0),
                "double",
                new byte[]{64, 69, 0, 0, 0, 0, 0, 0},
                42.0
            ),
            Arguments.of(
                new BytecodeObject(42.0f),
                "float",
                new byte[]{66, 40, 0, 0},
                42.0f
            ),
            Arguments.of(
                new BytecodeObject(true),
                "bool",
                new byte[]{1},
                true
            ),
            Arguments.of(
                new BytecodeObject(false),
                "bool",
                new byte[]{0},
                false
            ),
            Arguments.of(
                new BytecodeObject("Hello!"),
                "string",
                new byte[]{72, 101, 108, 108, 111, 33},
                "Hello!"
            ),
            Arguments.of(
                new BytecodeObject(new byte[]{1, 2, 3}),
                "bytes",
                new byte[]{1, 2, 3},
                new byte[]{1, 2, 3}
            ),
            Arguments.of(
                new BytecodeObject(' '),
                "char",
                new byte[]{0, 32},
                ' '
            ),
            Arguments.of(
                new BytecodeObject(BytecodeObject.class),
                "class",
                "org/eolang/jeo/representation/bytecode/BytecodeObject".getBytes(
                    StandardCharsets.UTF_8
                ),
                BytecodeObject.class
            ),
            Arguments.of(
                new BytecodeObject(Type.INT_TYPE),
                "type",
                "I".getBytes(StandardCharsets.UTF_8),
                Type.INT_TYPE
            ),
            Arguments.of(
                new BytecodeObject(null),
                "nullable",
                null,
                null
            )
        );
    }
}
