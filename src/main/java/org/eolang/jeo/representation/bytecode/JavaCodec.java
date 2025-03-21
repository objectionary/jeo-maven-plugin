/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.objectweb.asm.Type;

/**
 * Plain codec.
 * Converts objects to bytes and vice versa using Java type sizes.
 * @since 0.8
 * @checkstyle CyclomaticComplexityCheck (500 lines)
 */
public final class JavaCodec implements Codec {

    /**
     * Empty bytes.
     */
    private static final byte[] EMPTY = new byte[0];

    @Override
    public byte[] encode(final Object value, final DataType type) {
        final byte[] result;
        switch (type) {
            case BOOL:
                result = JavaCodec.booleanBytes(value);
                break;
            case CHAR:
                result = JavaCodec.charBytes(value);
                break;
            case BYTE:
                result = ByteBuffer.allocate(Byte.BYTES).put((byte) value).array();
                break;
            case SHORT:
                result = ByteBuffer.allocate(Short.BYTES).putShort((short) value).array();
                break;
            case INT:
                result = ByteBuffer.allocate(Long.BYTES).putLong((int) value).array();
                break;
            case LONG:
                result = ByteBuffer.allocate(Long.BYTES).putLong((long) value).array();
                break;
            case FLOAT:
                result = ByteBuffer.allocate(Float.BYTES).putFloat((float) value).array();
                break;
            case DOUBLE:
                result = ByteBuffer.allocate(Double.BYTES).putDouble((double) value).array();
                break;
            case STRING:
                result = Optional.ofNullable(value).map(String::valueOf)
                    .map(unicode -> unicode.getBytes(StandardCharsets.UTF_8))
                    .orElse(null);
                break;
            case BYTES:
                result = byte[].class.cast(value);
                break;
            case LABEL:
                result = BytecodeLabel.class.cast(value).uid().getBytes(StandardCharsets.UTF_8);
                break;
            case TYPE_REFERENCE:
                result = JavaCodec.typeBytes(value);
                break;
            case CLASS_REFERENCE:
                result = JavaCodec.hexClass(Class.class.cast(value).getName());
                break;
            case NULL:
                result = JavaCodec.EMPTY;
                break;
            default:
                throw new UnsupportedDataType(type);
        }
        return result;
    }

    @Override
    public Object decode(final byte[] bytes, final DataType type) {
        final Object result;
        switch (type) {
            case BOOL:
                result = Boolean.valueOf(bytes[0] != 0);
                break;
            case CHAR:
                result = ByteBuffer.wrap(bytes).getChar();
                break;
            case BYTE:
                result = ByteBuffer.wrap(bytes).get();
                break;
            case SHORT:
                result = ByteBuffer.wrap(bytes).getShort();
                break;
            case INT:
                result = (int) ByteBuffer.wrap(bytes).getLong();
                break;
            case LONG:
                result = ByteBuffer.wrap(bytes).getLong();
                break;
            case FLOAT:
                result = ByteBuffer.wrap(bytes).getFloat();
                break;
            case DOUBLE:
                result = ByteBuffer.wrap(bytes).getDouble();
                break;
            case STRING:
                result = Optional.ofNullable(bytes)
                    .map(all -> new String(all, StandardCharsets.UTF_8))
                    .orElse("");
                break;
            case BYTES:
                result = bytes;
                break;
            case LABEL:
                result = new BytecodeLabel(new String(bytes, StandardCharsets.UTF_8));
                break;
            case TYPE_REFERENCE:
                result = Type.getType(String.format(new String(bytes, StandardCharsets.UTF_8)));
                break;
            case CLASS_REFERENCE:
                result = JavaCodec.classReference(bytes);
                break;
            case NULL:
                result = null;
                break;
            default:
                throw new UnsupportedDataType(type);
        }
        return result;
    }

    /**
     * Get class reference by bytes.
     * @param bytes Bytes.
     * @return Class.
     */
    private static Class<?> classReference(final byte[] bytes) {
        final String name = new String(bytes, StandardCharsets.UTF_8);
        try {
            return Class.forName(name.replace('/', '.'));
        } catch (final ClassNotFoundException exception) {
            throw new IllegalStateException(
                String.format("Class with name '%s' isn't found", name),
                exception
            );
        }
    }

    /**
     * Convert boolean to bytes.
     * @param value Boolean.
     * @return Bytes.
     */
    private static byte[] booleanBytes(final Object value) {
        final byte[] result;
        if (value instanceof Integer) {
            result = JavaCodec.hexBoolean((int) value != 0);
        } else {
            result = JavaCodec.hexBoolean(Boolean.class.cast(value));
        }
        return result;
    }

    /**
     * Convert char to bytes.
     * @param value Char.
     * @return Bytes.
     */
    private static byte[] charBytes(final Object value) {
        final char val;
        if (value instanceof Integer) {
            val = (char) (int) value;
        } else {
            val = (char) value;
        }
        return ByteBuffer.allocate(Character.BYTES).putChar(val).array();
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
     * Convert a type to bytes.
     * @param value Type.
     * @return Bytes.
     */
    private static byte[] typeBytes(final Object value) {
        try {
            return JavaCodec.hexClass(((Type) value).getDescriptor());
        } catch (final AssertionError exception) {
            throw new IllegalStateException(
                String.format("Failed to get class name for %s", value), exception
            );
        }
    }

    /**
     * Convert class name to bytes.
     * @param name Class name.
     * @return Bytes.
     */
    private static byte[] hexClass(final String name) {
        return name.replace('.', '/').getBytes(StandardCharsets.UTF_8);
    }
}
