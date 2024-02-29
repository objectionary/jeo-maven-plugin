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

    /**
     * All supported data types.
     * @since 0.3
     */
    private enum DataType {
        /**
         * Boolean.
         */
        BOOL(
            "bool",
            Boolean.class,
            data -> DataType.hexBoolean(Boolean.class.cast(data))
        ),

        /**
         * Integer.
         */
        INT(
            "int",
            Integer.class,
            data -> ByteBuffer.allocate(Long.BYTES).putLong((int) data).array()
        ),
        /**
         * Long.
         */
        LONG(
            "long",
            Long.class,
            data -> ByteBuffer.allocate(Long.BYTES).putLong((long) data).array()
        ),

        /**
         * Float.
         */
        FLOAT(
            "float",
            Float.class,
            data -> ByteBuffer.allocate(Long.BYTES).putFloat((float) data).array()
        ),

        /**
         * Double.
         */
        DOUBLE(
            "double",
            Double.class,
            data -> ByteBuffer.allocate(Long.BYTES).putDouble((double) data).array()
        ),

        /**
         * String.
         */
        STRING(
            "string",
            String.class,
            data -> String.valueOf(data).getBytes(StandardCharsets.UTF_8)
        ),

        /**
         * Bytes.
         */
        BYTES("bytes", byte[].class, byte[].class::cast),

        /**
         * Label.
         */
        LABEL("label", Label.class, data -> new byte[0]),

        /**
         * Class reference.
         */
        CLASS_REFERENCE(
            "reference",
            Class.class,
            data -> DataType.hexClass(Class.class.cast(data).getName())
        ),

        /**
         * Type reference.
         */
        TYPE_REFERENCE(
            "reference",
            Type.class,
            data -> DataType.hexClass(Type.class.cast(data).getClassName())
        );

        /**
         * Base type.
         */
        private final String base;

        /**
         * Class.
         */
        private final Class<?> clazz;

        /**
         * Converter.
         */
        private final Function<Object, byte[]> converter;

        /**
         * Constructor.
         * @param base Base type.
         * @param klass Class.
         * @param converter Converter.
         */
        DataType(
            final String base,
            final Class<?> klass,
            final Function<Object, byte[]> converter
        ) {
            this.base = base;
            this.clazz = klass;
            this.converter = converter;
        }


        /**
         * Get a type for some data.
         * @param data Some data.
         * @return Type.
         */
        private static String type(final Object data) {
            return DataType.from(data).base;
        }

        /**
         * Convert data to hex.
         * @param data Data.
         * @return Hex representation of data.
         */
        private static byte[] hex(final Object data) {
            return DataType.from(data).converter.apply(data);
        }

        /**
         * Convert boolean to bytes.
         * @param data Boolean.
         * @return Bytes.
         */
        private static byte[] hexBoolean(final boolean data) {
            final byte[] result;
            if (data) {
                result = new byte[]{0x01};
            } else {
                result = new byte[]{0x00};
            }
            return result;
        }

        /**
         * Convert class name to bytes.
         * @param name Class name.
         * @return Bytes.
         */
        private static byte[] hexClass(final String name) {
            return name.replace('.', '/').getBytes(StandardCharsets.UTF_8);
        }

        /**
         * Get a data type for some data.
         * @param data Data.
         * @return Data type.
         */
        private static DataType from(final Object data) {
            return Arrays.stream(DataType.values()).filter(type -> type.clazz.isInstance(data))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown data type"));
        }
    }
}
