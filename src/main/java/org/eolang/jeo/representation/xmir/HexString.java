/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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

/**
 * Hex string.
 * @since 0.1.0
 */
public final class HexString {

    /**
     * Hex radix.
     */
    private static final int RADIX = 16;

    /**
     * Hex string.
     * Example:
     * - "48 65 6C 6C 6F 20 57 6F 72 6C 64 21"
     */
    private final String hex;

    /**
     * Constructor.
     * @param hex Hex string.
     */
    public HexString(final String hex) {
        this.hex = hex;
    }

    /**
     * Convert hex string to human-readable string.
     * Example:
     *  "48 65 6C 6C 6F 20 57 6F 72 6C 64 21" -> "Hello World!"
     * @return Human-readable string.
     */
    public String decode() {
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
    public int decodeAsInt() {
        return Integer.parseInt(this.hex.trim().replace(" ", ""), HexString.RADIX);
    }

    /**
     * Convert hex string to boolean.
     * @return Boolean.
     */
    public boolean decodeAsBoolean() {
        final String value = this.hex.trim();
        if (value.length() != 2) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid hex boolean string: %s, the expected size is 2: 01 or 00",
                    this.hex
                )
            );
        }
        return value.equals("01");
    }
}
