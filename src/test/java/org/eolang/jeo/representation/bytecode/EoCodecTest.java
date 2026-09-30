/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link EoCodec}.
 *
 * @since 0.8
 */
final class EoCodecTest {

    @Test
    void keepsTheBitsOfSignalingNanFloat() {
        final EoCodec codec = new EoCodec();
        MatcherAssert.assertThat(
            "A signaling NaN float must come back with the same bits",
            Float.floatToRawIntBits(
                (float) codec.decode(
                    codec.encode(Float.intBitsToFloat(0x7f800001), DataType.FLOAT),
                    DataType.FLOAT
                )
            ),
            Matchers.equalTo(0x7f800001)
        );
    }

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
            {"Hello, world!", DataType.STRING, "Hello, world!".getBytes(StandardCharsets.UTF_8)},
        };
    }
}
