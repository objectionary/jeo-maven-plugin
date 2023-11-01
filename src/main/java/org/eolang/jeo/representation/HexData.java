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
package org.eolang.jeo.representation;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

/**
 * Hexadecimal data.
 * @since 0.1
 */
public final class HexData {

    /**
     * Data to convert.
     */
    private final Object data;

    /**
     * Constructor.
     * @param data Data.
     */
    public HexData(final Object data) {
        this.data = data;
    }

    /**
     * Value of the data.
     * @return Value
     */
    public String value() {
        final byte[] res;
        if (this.data instanceof String) {
            res = ((String) this.data).getBytes(StandardCharsets.UTF_8);
        } else {
            res = ByteBuffer
                .allocate(Long.BYTES)
                .putLong((int) this.data)
                .array();
        }
        return HexData.bytesToHex(res);
    }

    /**
     * Type of the data.
     * @return Type
     */
    public String type() {
        final String res;
        if (this.data instanceof String) {
            res = "string";
        } else if (this.data instanceof Integer) {
            res = "int";
        } else if (this.data instanceof Float || this.data instanceof Double) {
            res = "float";
        } else if (this.data instanceof Boolean) {
            res = "bool";
        } else {
            res = "bytes";
        }
        return res;
    }

    /**
     * Bytes to HEX.
     *
     * @param bytes Bytes.
     * @return Hexadecimal value as string.
     */
    private static String bytesToHex(final byte... bytes) {
        final StringJoiner out = new StringJoiner(" ");
        for (final byte bty : bytes) {
            out.add(String.format("%02X", bty));
        }
        return out.toString();
    }
}
