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
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Predicate;
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
        return HexData.bytesToHex(DataType.hex(this.data));
    }

    /**
     * Type of the data.
     * @return Type
     */
    public String type() {
        return DataType.type(this.data);
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
        STRING(
            "string",
            data -> String.valueOf(data).getBytes(StandardCharsets.UTF_8),
            String.class
        ),
        INT(
            "int",
            data -> ByteBuffer.allocate(Long.BYTES).putLong((int) data).array(),
            Integer.class
        ),
        LONG(
            "long",
            data -> ByteBuffer.allocate(Long.BYTES).putLong((long) data).array(),
            Long.class
        ),
        FLOAT(
            "float",
            data -> ByteBuffer.allocate(Long.BYTES).putFloat((float) data).array(),
            Float.class
        ),
        DOUBLE(
            "double",
            data -> ByteBuffer.allocate(Long.BYTES).putDouble((double) data).array(),
            Double.class
        ),
        BOOL(
            "bool",
            data -> {
                return ((boolean) data) ? new byte[]{0x01} : new byte[]{0x00};
            },
            Boolean.class
        ),
        BYTES(
            "bytes",
            data -> (byte[]) data,
            byte[].class
        ),
        LABEL(
            "label",
            data -> new byte[0],
            Label.class
        ),

        REFERENCE_CLASS("reference", data -> {
            final byte[] res;
            if (data instanceof Class<?>) {
                res = ((Class<?>) data).getName().replace('.', '/')
                    .getBytes(StandardCharsets.UTF_8);
            } else {
                res = ((Type) data).getClassName().replace('.', '/')
                    .getBytes(StandardCharsets.UTF_8);
            }
            return res;
        }, Class.class),
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
            },
            Type.class
        );

        private final String base;

        private final Function<Object, byte[]> converter;

        private final Class<?> claazz;

        DataType(
            final String base,
            final Function<Object, byte[]> converter,
            final Class<?> predicate
        ) {
            this.base = base;
            this.converter = converter;
            this.claazz = predicate;
        }

        private static String type(final Object data) {
            return DataType.from(data).base;
        }

        private static byte[] hex(final Object data) {
            return DataType.from(data).converter.apply(data);
        }

        private static DataType from(final Object data) {
            return Arrays.stream(DataType.values()).filter(type -> type.claazz.isInstance(data))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown data type"));
        }
    }
}
