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
package org.eolang.jeo.representation.xmir;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.DataType;

/**
 * Hex string.
 * @since 0.1.0
 */
final class HexString {

    /**
     * Hex radix.
     */
    private static final int RADIX = 16;

    /**
     * Hex string.
     * Example:
     * - "20 57 6F 72 6C 64 21" (World!)
     */
    private final String hex;

    /**
     * Constructor.
     * @param hex Hex string.
     */
    HexString(final String hex) {
        this.hex = hex;
    }

    /**
     * Convert hex string to human-readable string.
     * Example:
     *  "48 65 6C 6C 6F 20 57 6F 72 6C 64 21" -> "Hello World!"
     * @return Human-readable string.
     */
    String decode() {
        try {
            final String result;
            if (this.hex.isEmpty()) {
                result = "";
            } else {
                result = Arrays.stream(this.hex.split(" "))
                    .map(ch -> (char) Integer.parseInt(ch, HexString.RADIX))
                    .map(String::valueOf)
                    .collect(Collectors.joining());
            }
            return result;
        } catch (final NumberFormatException exception) {
            throw new IllegalArgumentException(
                String.format("Invalid hex string: %s", this.hex),
                exception
            );
        }
    }

    /**
     * Convert hex string to integer.
     * @return Integer.
     */
    int decodeAsInt() {
        return Integer.parseInt(this.clean(), HexString.RADIX);
    }

    /**
     * Convert hex string to long.
     * @return Long.
     */
    long decodeAsLong() {
        return Long.parseLong(this.clean(), HexString.RADIX);
    }

    /**
     * Convert hex string to boolean.
     * @return Boolean.
     */
    boolean decodeAsBoolean() {
        final String value = this.clean();
        if (value.length() != 2) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid hex boolean string: %s, the expected size is 2: 01 or 00",
                    this.hex
                )
            );
        }
        return "01".equals(value);
    }

    /**
     * Convert hex string to double.
     * @return Double.
     */
    double decodeAsDouble() {
        return (double) DataType.DOUBLE.decode(this.clean());
    }

    /**
     * Convert hex string to float.
     * @return Float.
     */
    float decodeAsFloat() {
        return (float) DataType.FLOAT.decode(this.clean());
    }

    private String clean() {
        return this.hex.trim().replaceAll(" ", "");
    }

    /**
     * Convert bytes to data.
     * @param raw Bytes.
     * @return Data.
     */
    public byte[] decode(final String raw) {
        final byte[] result;
        if (raw == null) {
            result = null;
        } else {
            final char[] chars = raw.trim().replace(" ", "").toCharArray();
            final int length = chars.length;
            final byte[] res = new byte[length / 2];
            for (int index = 0; index < length; index += 2) {
                res[index / 2] = (byte) Integer.parseInt(
                    String.copyValueOf(new char[]{chars[index], chars[index + 1]}), 16
                );
            }
            result = res;
        }
        return result;
    }

    public boolean isEmpty() {
        return this.hex.isEmpty();
    }
}
