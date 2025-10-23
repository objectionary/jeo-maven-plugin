/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link BytecodeValue}.
 * @since 0.6
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class BytecodeValueTest {

    @ParameterizedTest
    @MethodSource("arguments")
    void detectsType(
        final BytecodeValue value,
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
        final BytecodeValue value,
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
                new BytecodeValue(42),
                "number",
                new byte[]{0, 0, 0, 0, 0, 0, 0, 42},
                42
            ),
            Arguments.of(
                new BytecodeValue(42L),
                "long",
                new byte[]{0, 0, 0, 0, 0, 0, 0, 42},
                42L
            ),
            Arguments.of(
                new BytecodeValue(42.0),
                "double",
                new byte[]{64, 69, 0, 0, 0, 0, 0, 0},
                42.0
            ),
            Arguments.of(
                new BytecodeValue(42.0f),
                "float",
                new byte[]{66, 40, 0, 0},
                42.0f
            ),
            Arguments.of(
                new BytecodeValue(true),
                "bool",
                new byte[]{1},
                true
            ),
            Arguments.of(
                new BytecodeValue(false),
                "bool",
                new byte[]{0},
                false
            ),
            Arguments.of(
                new BytecodeValue("Hello!"),
                "string",
                new byte[]{72, 101, 108, 108, 111, 33},
                "Hello!"
            ),
            Arguments.of(
                new BytecodeValue(new byte[]{1, 2, 3}),
                "bytes",
                new byte[]{1, 2, 3},
                new byte[]{1, 2, 3}
            ),
            Arguments.of(
                new BytecodeValue(' '),
                "char",
                new byte[]{0, 32},
                ' '
            ),
            Arguments.of(
                new BytecodeValue(null),
                "nullable",
                null,
                null
            )
        );
    }
}
