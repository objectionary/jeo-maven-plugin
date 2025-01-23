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
            {"Hello, world!", DataType.STRING, "Hello, world!".getBytes()}
        };
    }
}