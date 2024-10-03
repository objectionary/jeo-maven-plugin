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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeValue;

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
     * Space pattern.
     */
    private static final Pattern SPACE = Pattern.compile(" ");

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
     * Convert hex string to a byte array.
     * @return Byte array.
     */
    public byte[] bytes() {
        final String hex = this.hex();
        final byte[] res;
        if (hex.isEmpty()) {
            res = null;
        } else {
            final char[] chars = hex.toCharArray();
            final int length = chars.length;
            res = new byte[length / 2];
            for (int index = 0; index < length; index += 2) {
                res[index / 2] = (byte) Integer.parseInt(
                    String.copyValueOf(new char[]{chars[index], chars[index + 1]}), XmlValue.RADIX
                );
            }
        }
        return res;
    }

    /**
     * Convert hex string to an object.
     * @return Object.
     */
    public Object object() {
        final String base = this.base();
        final Object result;
        if (base.equals("string")) {
            result = this.string();
        } else {
            result = new BytecodeValue(base, this.bytes()).object();
        }
        return result;
    }

    /**
     * Hex string.
     * Example:
     * - "20 57 6F 72 6C 64 21" -> "20576F726C6421"
     * @return Hex string.
     */
    private String hex() {
        return XmlValue.SPACE.matcher(this.node.firstChild().text().trim()).replaceAll("");
    }

    /**
     * Get the type of the object without a package.
     * @return Type without package.
     */
    private String base() {
        final String base = this.node.attribute("base")
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "'%s' is not an argument because it doesn't have 'base' attribute",
                        this.node
                    )
                )
            );
        final String result;
        final int last = base.lastIndexOf('.');
        if (last == -1) {
            result = base;
        } else {
            result = base.substring(last + 1);
        }
        return result;
    }
}
