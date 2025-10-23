/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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

    @ParameterizedTest
    @MethodSource("mapping")
    void decodesSuccessfully(final Object value, final DataType type, final byte[] bytes) {
        MatcherAssert.assertThat(
            "Can't decode byte array to the correct value",
            new JavaCodec().decode(bytes, type),
            Matchers.equalTo(value)
        );
    }

    /**
     * Test cases.
     * @return Arguments.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Object[][] mapping() {
        return new Object[][]{
            {null, DataType.NULL, JavaCodecTest.EMPTY},
            {true, DataType.BOOL, new byte[]{1}},
            {'a', DataType.CHAR, new byte[]{0, 97}},
            {new byte[]{0, 1, 2, 3}, DataType.BYTES, new byte[]{0, 1, 2, 3}},
            {"hello, world!", DataType.STRING, "hello, world!".getBytes(StandardCharsets.UTF_8)},
        };
    }
}
