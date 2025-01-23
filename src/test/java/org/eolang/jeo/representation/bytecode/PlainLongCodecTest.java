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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test cases for {@link PlainLongCodec}.
 * @since 0.8
 */
final class PlainLongCodecTest {

    @ParameterizedTest
    @MethodSource("mapping")
    void encodesSuccessfully(final Object value, final DataType type, final byte[] bytes) {
        MatcherAssert.assertThat(
            "Can't encode value to the correct byte array using PlainLongCodec",
            new PlainLongCodec(new EoCodec()).encode(value, type),
            Matchers.equalTo(bytes)
        );
    }

    @ParameterizedTest
    @MethodSource("mapping")
    void decodesSuccessfully(final Object value, final DataType type, final byte[] bytes) {
        MatcherAssert.assertThat(
            "Can't decode byte array to the correct value using PlainLongCodec",
            new PlainLongCodec(new EoCodec()).decode(bytes, type),
            Matchers.equalTo(value)
        );
    }

    /**
     * Test cases.
     * @return Arguments.
     */
    private static Object[] mapping() {
        return new Object[]{
            new Object[]{0L, DataType.LONG, new byte[]{0, 0, 0, 0, 0, 0, 0, 0}},
            new Object[]{1L, DataType.LONG, new byte[]{0, 0, 0, 0, 0, 0, 0, 1}},
            new Object[]{-1L, DataType.LONG, new byte[]{-1, -1, -1, -1, -1, -1, -1, -1}},
            new Object[]{42L, DataType.LONG, new byte[]{0, 0, 0, 0, 0, 0, 0, 42}},
            new Object[]{-42L, DataType.LONG, new byte[]{-1, -1, -1, -1, -1, -1, -1, -42}},
            new Object[]{0.0d, DataType.DOUBLE, new byte[]{0, 0, 0, 0, 0, 0, 0, 0}},
            new Object[]{1.0d, DataType.DOUBLE, new byte[]{63, -16, 0, 0, 0, 0, 0, 0}},
            new Object[]{-1.0d, DataType.DOUBLE, new byte[]{-65, -16, 0, 0, 0, 0, 0, 0}},
            new Object[]{42.0d, DataType.DOUBLE, new byte[]{64, 69, 0, 0, 0, 0, 0, 0}},
            new Object[]{-42.0d, DataType.DOUBLE, new byte[]{-64, 69, 0, 0, 0, 0, 0, 0}},
            new Object[]{"0", DataType.STRING, "0".getBytes(StandardCharsets.UTF_8)},
            new Object[]{"1", DataType.STRING, "1".getBytes(StandardCharsets.UTF_8)},
            new Object[]{"-1", DataType.STRING, "-1".getBytes(StandardCharsets.UTF_8)},
            new Object[]{"42", DataType.STRING, "42".getBytes(StandardCharsets.UTF_8)},
        };
    }
}