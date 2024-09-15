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
package org.eolang.jeo.representation;

import lombok.ToString;

/**
 * Hexadecimal data.
 * @since 0.1
 */
@ToString
public final class HexData {

    /**
     * Array of hexadecimal characters.
     * Used for converting bytes to hexadecimal.
     * See {@link #bytesToHex(byte[])}.
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Data to convert.
     */
    private final Object data;

    /**
     * Constructor.
     * @param data Data.
     * @param <T> Data type.
     */
    public <T> HexData(final T data) {
        this.data = data;
    }

    /**
     * Value of the data.
     * @return Value
     */
    public String value() {
        return HexData.bytesToHex(DataType.toBytes(this.data));
    }

    /**
     * Type of the data.
     * @return Type
     */
    public String type() {
        return DataType.type(this.data);
    }

    /**
     * Is it null?
     * @return TRUE if it's null
     */
    public boolean isNull() {
        return this.data == null;
    }

    /**
     * Bytes to HEX.
     * The efficient way to convert bytes to hexadecimal.
     * ATTENTION!
     * Do not modify this method.
     * It is an optimized version that saves memory and CPU.
     * Actually, the solution is based on the following StackOverflow answer:
     * <a href="https://stackoverflow.com/a/9855338/10423604">here</a>
     * You can find the full explanation or any other examples there.
     *
     * @param bytes Bytes.
     * @return Hexadecimal value as string.
     */
    private static String bytesToHex(final byte[] bytes) {
        final String res;
        if (bytes == null || bytes.length == 0) {
            res = "";
        } else {
            final char[] hex = new char[bytes.length * 3];
            for (int index = 0; index < bytes.length; ++index) {
                final int value = bytes[index] & 0xFF;
                hex[index * 3] = HexData.HEX_ARRAY[value >>> 4];
                hex[index * 3 + 1] = HexData.HEX_ARRAY[value & 0x0F];
                hex[index * 3 + 2] = ' ';
            }
            res = new String(hex, 0, hex.length - 1);
        }
        return res;
    }
}
