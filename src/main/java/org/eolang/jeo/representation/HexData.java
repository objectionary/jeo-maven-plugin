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
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.FieldVisitor;
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
    public enum DataType {
        /**
         * Boolean.
         */
        BOOL("bool", Boolean.class, value ->
            DataType.hexBoolean(Boolean.class.cast(value)),
            bytes -> {
                return Boolean.valueOf(bytes[0] != 0);
            }
        ),

        /**
         * Integer.
         */
        INT("int", Integer.class, value ->
            ByteBuffer.allocate(Long.BYTES).putLong((int) value).array(),
            bytes -> (int) ByteBuffer.wrap(bytes).getLong()
        ),
        /**
         * Long.
         */
        LONG("long", Long.class, value ->
            ByteBuffer.allocate(Long.BYTES).putLong((long) value).array(),
            bytes -> ByteBuffer.wrap(bytes).getLong()
        ),

        /**
         * Float.
         */
        FLOAT("float", Float.class, value ->
            ByteBuffer.allocate(Float.BYTES).putFloat((float) value).array(),
            bytes -> ByteBuffer.wrap(bytes).getFloat()
        ),

        /**
         * Double.
         */
        DOUBLE("double", Double.class, value ->
            ByteBuffer.allocate(Double.BYTES).putDouble((double) value).array(),
            bytes -> ByteBuffer.wrap(bytes).getDouble()
        ),

        /**
         * String.
         */
        STRING("string", String.class, value ->
            String.valueOf(value).getBytes(StandardCharsets.UTF_8),
            bytes -> new String(bytes, StandardCharsets.UTF_8)
        ),

        /**
         * Bytes.
         */
        BYTES("bytes", byte[].class, value -> byte[].class.cast(value),
            bytes -> bytes
        ),

        /**
         * Label.
         */
        LABEL("label", Label.class, value ->
            new AllLabels().uid(Label.class.cast(value)).getBytes(StandardCharsets.UTF_8),
            bytes -> new AllLabels().label(new String(bytes, StandardCharsets.UTF_8))
        ),

        /**
         * Type reference.
         */
        TYPE_REFERENCE("reference", Type.class, value ->
            DataType.hexClass(Type.class.cast(value).getClassName()),
            bytes -> Type.getType(String.format("L%s;", new String(bytes, StandardCharsets.UTF_8)))
        ),

        /**
         * Class reference.
         */
        CLASS_REFERENCE("reference", Class.class, value ->
            DataType.hexClass(Class.class.cast(value).getName()),
            bytes -> new String(bytes, StandardCharsets.UTF_8)
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
         * Converter to hex.
         */
        private final Function<Object, byte[]> encoder;

        private final Function<byte[], Object> decoder;

        /**
         * Constructor.
         * @param base Base type.
         * @param clazz Class.
         * @param encoder Converter to hex.
         * @param decoder Converter from hex.
         */
        DataType(
            final String base,
            final Class<?> clazz,
            final Function<Object, byte[]> encoder,
            final Function<byte[], Object> decoder
        ) {
            this.base = base;
            this.clazz = clazz;
            this.encoder = encoder;
            this.decoder = decoder;
        }

        public static DataType byBase(final String base) {
            return Arrays.stream(DataType.values())
                .filter(type -> type.base.equals(base))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        String.format("Unknown data type '%s'", base)
                    )
                );
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
        private static byte[] toBytes(final Object data) {
            return DataType.from(data).encoder.apply(data);
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

        private static Class<?> decodeClass(final byte[] bytes) {
            final String clazz = new String(bytes, StandardCharsets.UTF_8).replace('/', '.');
            try {
                return Class.forName(clazz);
            } catch (final ClassNotFoundException exception) {
                throw new IllegalArgumentException(
                    String.format("Class not found: %s", clazz),
                    exception
                );
            }
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

        public Object decode(final String raw) {
            final char[] chars = raw.trim().replace(" ", "").toCharArray();
            final int length = chars.length;
            final byte[] res = new byte[length / 2];
            for (int i = 0; i < length; i += 2) {
                res[i / 2] = (byte) Integer.parseInt(
                    String.copyValueOf(new char[]{chars[i], chars[i + 1]}), 16
                );
            }
            return this.decoder.apply(res);
        }
    }
}
