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
package org.eolang.jeo.representation.bytecode;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

/**
 * All supported data types.
 * @since 0.3
 */
enum DataType {

    /**
     * Boolean.
     */
    BOOL("bool", Boolean.class,
        value -> {
            final byte[] result;
            if (value instanceof Integer) {
                result = DataType.hexBoolean((int) value != 0);
            } else {
                result = DataType.hexBoolean(Boolean.class.cast(value));
            }
            return result;
        },
        bytes -> {
            return Boolean.valueOf(bytes[0] != 0);
        }
    ),

    /**
     * Character.
     */
    CHAR("char", Character.class,
        value -> {
            final char val;
            if (value instanceof Integer) {
                val = (char) (int) value;
            } else {
                val = (char) value;
            }
            return ByteBuffer.allocate(Character.BYTES).putChar(val).array();
        },
        bytes -> ByteBuffer.wrap(bytes).getChar()
    ),

    /**
     * Byte.
     */
    BYTE("byte", Byte.class,
        value -> ByteBuffer.allocate(Byte.BYTES).put((byte) (int) value).array(),
        bytes -> ByteBuffer.wrap(bytes).get()
    ),

    /**
     * Short.
     */
    SHORT("short", Short.class,
        value -> ByteBuffer.allocate(Short.BYTES).putShort((short) (int) value).array(),
        bytes -> ByteBuffer.wrap(bytes).getShort()
    ),

    /**
     * Integer.
     */
    INT("int", Integer.class,
        value -> ByteBuffer.allocate(Long.BYTES).putLong((int) value).array(),
        bytes -> (int) ByteBuffer.wrap(bytes).getLong()
    ),
    /**
     * Long.
     */
    LONG("long", Long.class,
        value -> ByteBuffer.allocate(Long.BYTES).putLong((long) value).array(),
        bytes -> ByteBuffer.wrap(bytes).getLong()
    ),

    /**
     * Float.
     */
    FLOAT("float", Float.class,
        value -> ByteBuffer.allocate(Float.BYTES).putFloat((float) value).array(),
        bytes -> ByteBuffer.wrap(bytes).getFloat()
    ),

    /**
     * Double.
     */
    DOUBLE("double", Double.class,
        value -> ByteBuffer.allocate(Double.BYTES).putDouble((double) value).array(),
        bytes -> ByteBuffer.wrap(bytes).getDouble()
    ),

    /**
     * String.
     */
    STRING("string", String.class,
        value -> Optional.ofNullable(value).map(String::valueOf)
            .map(unicode -> unicode.getBytes(StandardCharsets.UTF_8))
            .orElse(null),
        bytes -> new String(bytes, StandardCharsets.UTF_8)
    ),

    /**
     * Bytes.
     */
    BYTES("bytes", byte[].class,
        value -> byte[].class.cast(value),
        bytes -> bytes
    ),

    /**
     * Label.
     */
    LABEL("label", BytecodeLabel.class,
        value -> BytecodeLabel.class.cast(value).uid().getBytes(StandardCharsets.UTF_8),
        bytes -> new BytecodeLabel(new String(bytes, StandardCharsets.UTF_8))
    ),

    /**
     * Type reference.
     */
    TYPE_REFERENCE(
        "type", Type.class, DataType::typeBytes, bytes ->
        Type.getType(String.format(new String(bytes, StandardCharsets.UTF_8)))
    ),

    /**
     * Class reference.
     */
    CLASS_REFERENCE("class", Class.class,
        value -> DataType.hexClass(Class.class.cast(value).getName()),
        bytes -> new String(bytes, StandardCharsets.UTF_8)
    ),

    /**
     * Null.
     */
    NULL("nullable", null, value -> new byte[0], bytes -> null);

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

    /**
     * Converter from hex.
     */
    private final Function<byte[], Object> decoder;

    /**
     * Constructor.
     * @param base Base type.
     * @param clazz Class.
     * @param encoder Converter to hex.
     * @param decoder Converter from hex.
     * @checkstyle ParameterNumberCheck (5 lines)
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

    /**
     * Find a type by base.
     * @param base Base type.
     * @return Type.
     */
    static DataType findByBase(final String base) {
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
     * Find a type by data.
     * @param data Data.
     * @return Data type.
     */
    static DataType findByData(final Object data) {
        final DataType result;
        if (data == null) {
            result = DataType.NULL;
        } else {
            result = Arrays.stream(DataType.values()).filter(type -> type.clazz.isInstance(data))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        String.format(
                            "Unknown data type of %s, class is %s",
                            data,
                            Optional.ofNullable(data)
                                .map(Object::getClass)
                                .map(Class::getName)
                                .orElse("Nullable")
                        )
                    )
                );
        }
        return result;
    }

    /**
     * Type name.
     * @return Type name.
     */
    String caption() {
        return this.base;
    }

    /**
     * Encode data.
     * @param data Data.
     * @return Encoded data.
     */
    byte[] encode(final Object data) {
        return Optional.ofNullable(data).map(this.encoder).orElse(null);
    }

    /**
     * Decode data.
     * @param data Data.
     * @return Decoded data.
     */
    Object decode(final byte[] data) {
        return Optional.ofNullable(data).map(this.decoder).orElse(null);
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
     * Convert type to bytes.
     * @param value Type.
     * @return Bytes.
     */
    private static byte[] typeBytes(final Object value) {
        try {
            return DataType.hexClass(((Type) value).getDescriptor());
        } catch (final AssertionError exception) {
            throw new IllegalStateException(
                String.format("Failed to get class name for %s", value), exception
            );
        }
    }
}
