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

/**
 * XML value.
 * @since 0.6
 */
public final class XmlValue {

    /**
     * Hex radix.
     */
    private static final int RADIX = 16;

    /**
     * XML node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node XML node.
     */
    public XmlValue(final XmlNode node) {
        this.node = node;
    }

    /**
     * Convert hex string to human-readable string.
     * Example:
     *  "48 65 6C 6C 6F 20 57 6F 72 6C 64 21" -> "Hello World!"
     * @return Human-readable string.
     */
    public String string() {
        final String hex = this.hex();
        try {
            final String result;
            if (hex.isEmpty()) {
                result = "";
            } else {
                result = Arrays.stream(hex.split("(?<=\\G.{2})"))
                    .map(ch -> (char) Integer.parseInt(ch, XmlValue.RADIX))
                    .map(String::valueOf)
                    .collect(Collectors.joining());
            }
            return result;
        } catch (final NumberFormatException exception) {
            throw new IllegalArgumentException(
                String.format("Invalid hex string: %s", hex),
                exception
            );
        }
    }


    /**
     * Convert hex string to boolean.
     * @return Boolean.
     */
    public boolean bool() {
        final String value = this.hex();
        if (value.length() != 2) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid hex boolean string: %s, the expected size is 2: 01 or 00",
                    value
                )
            );
        }
        return "01".equals(value);
    }

    /**
     * Convert hex string to integer.
     * @return Integer.
     */
    public int integer() {
        return Integer.parseInt(this.hex(), XmlValue.RADIX);
    }

    /**
     * Hex string.
     * Example:
     * - "20 57 6F 72 6C 64 21" -> "20576F726C6421"
     * @return Hex string.
     */
    private String hex() {
        return this.node.firstChild().text().trim().replaceAll(" ", "");
    }
}
