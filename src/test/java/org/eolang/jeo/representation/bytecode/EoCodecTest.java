/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link EoCodec}.
 * @since 0.8
 */
final class EoCodecTest {

    @ParameterizedTest
    @MethodSource("mapping")
    void encodesSuccessfully(final Object value, final DataType type, final byte[] bytes) {
        MatcherAssert.assertThat(
            "Can't encode value to the correct double byte array (according with IEEE 754)",
            new EoCodec().encode(value, type),
            Matchers.equalTo(bytes)
        );
    }

    @ParameterizedTest
    @MethodSource("mapping")
    void decodesSuccessfully(final Object value, final DataType type, final byte[] bytes) {
        MatcherAssert.assertThat(
            "Can't decode double byte array to the correct value (according with IEEE 754)",
            new EoCodec().decode(bytes, type),
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
            {true, DataType.BOOL, new byte[]{1}},
            {false, DataType.BOOL, new byte[]{0}},
            {'a', DataType.CHAR, new byte[]{0, 97}},
            {(byte) 42, DataType.BYTE, new byte[]{64, 69, 0, 0, 0, 0, 0, 0}},
            {(short) 42, DataType.SHORT, new byte[]{64, 69, 0, 0, 0, 0, 0, 0}},
            {42, DataType.INT, new byte[]{64, 69, 0, 0, 0, 0, 0, 0}},
            {42L, DataType.LONG, new byte[]{64, 69, 0, 0, 0, 0, 0, 0}},
            {42.0f, DataType.FLOAT, new byte[]{64, 69, 0, 0, 0, 0, 0, 0}},
            {42.0, DataType.DOUBLE, new byte[]{64, 69, 0, 0, 0, 0, 0, 0}},
            {"Hello, world!", DataType.STRING, "Hello, world!".getBytes()},
        };
    }
}
