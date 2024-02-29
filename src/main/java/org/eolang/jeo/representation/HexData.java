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
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

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
    public <T> HexData(final T data) {
        this.data = data;
    }

    /**
     * Value of the data.
     * @return Value
     */
    public String value() {
        return HexData.bytesToHex(DataType.fromRaw(this.data).convert(this.data));
    }

    /**
     * Type of the data.
     * @return Type
     */
    public String type() {
        return DataType.fromRaw(this.data).type();
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

    private enum DataType {
        STRING("string", data -> {
            return ((String) data).getBytes(StandardCharsets.UTF_8);
        }),
        INT("int", data -> ByteBuffer.allocate(Long.BYTES).putLong((int) data).array()),
        LONG("long", data -> ByteBuffer.allocate(Long.BYTES).putLong((long) data).array()),
        FLOAT("float", data -> ByteBuffer.allocate(Long.BYTES).putFloat((float) data).array()),
        DOUBLE("double", data -> ByteBuffer.allocate(Long.BYTES).putDouble((double) data).array()),
        BOOL("bool", data -> {
            return ((boolean) data) ? new byte[]{0x01} : new byte[]{0x00};
        }),
        BYTES("bytes", data -> {
            return (byte[]) data;
        }),
        LABEL("label", data -> new byte[0]),
        REFERENCE(
            "reference",
            data -> {
                final byte[] res;
                if (data instanceof Class<?>) {
                    res = ((Class<?>) data).getName().replace('.', '/')
                        .getBytes(StandardCharsets.UTF_8);
                } else {
                    res = ((Type) data).getClassName().replace('.', '/')
                        .getBytes(StandardCharsets.UTF_8);
                }
                return res;
            }
        );

        private final String base;

        private final Function<Object, byte[]> converter;

        DataType(final String base, final Function<Object, byte[]> converter) {
            this.base = base;
            this.converter = converter;
        }

        String type() {
            return this.base;
        }


        public byte[] convert(final Object data) {
            return this.converter.apply(data);
        }


        static DataType fromRaw(final Object data) {
            final DataType res;
            if (data instanceof String) {
                res = DataType.STRING;
            } else if (data instanceof Integer) {
                res = DataType.INT;
            } else if (data instanceof Float) {
                res = DataType.FLOAT;
            } else if (data instanceof Double) {
                res = DataType.DOUBLE;
            } else if (data instanceof Boolean) {
                res = DataType.BOOL;
            } else if (data instanceof Label) {
                res = DataType.LABEL;
            } else if (data instanceof Class<?> || data instanceof Type) {
                res = DataType.REFERENCE;
            } else {
                res = DataType.BYTES;
            }
            return res;
        }
    }
}
