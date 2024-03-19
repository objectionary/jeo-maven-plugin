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
import java.util.function.Function;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

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
    TYPE_REFERENCE(
        "type", Type.class, DataType::typeBytes, bytes ->
        Type.getType(String.format(new String(bytes, StandardCharsets.UTF_8)))
    ),

    /**
     * Class reference.
     */
    CLASS_REFERENCE("class", Class.class, value ->
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
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static DataType find(final String base) {
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
     * Convert bytes to data.
     * @param raw Bytes.
     * @return Data.
     */
    public Object decode(final String raw) {
        final char[] chars = raw.trim().replace(" ", "").toCharArray();
        final int length = chars.length;
        final byte[] res = new byte[length / 2];
        for (int index = 0; index < length; index += 2) {
            res[index / 2] = (byte) Integer.parseInt(
                String.copyValueOf(new char[]{chars[index], chars[index + 1]}), 16
            );
        }
        return this.decoder.apply(res);
    }

    /**
     * Get a type for some data.
     * @param data Some data.
     * @return Type.
     */
    static String type(final Object data) {
        return DataType.from(data).base;
    }

    /**
     * Convert data to hex.
     * @param data Data.
     * @return Hex representation of data.
     */
    static byte[] toBytes(final Object data) {
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

    /**
     * Get a data type for some data.
     * @param data Data.
     * @return Data type.
     */
    private static DataType from(final Object data) {
        return Arrays.stream(DataType.values()).filter(type -> type.clazz.isInstance(data))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "Unknown data type of %s, class is %s",
                        data,
                        data.getClass().getName()
                    )
                )
            );
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
