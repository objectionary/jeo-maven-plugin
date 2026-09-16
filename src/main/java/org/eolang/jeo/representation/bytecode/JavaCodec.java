/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

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
                    .map(JavaCodec::stringBytes)
                    .orElse(null);
                break;
            case BYTES:
                result = byte[].class.cast(value);
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
                    .map(JavaCodec::stringValue)
                    .orElse("");
                break;
            case BYTES:
                result = bytes;
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
     * Encode a Java string as UTF-8 while retaining lone surrogate code units.
     * @param value String to encode
     * @return Modified UTF-8 bytes
     */
    private static byte[] stringBytes(final String value) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (int idx = 0; idx < value.length(); ++idx) {
            final char current = value.charAt(idx);
            if (Character.isHighSurrogate(current)
                && idx + 1 < value.length()
                && Character.isLowSurrogate(value.charAt(idx + 1))) {
                final int code = Character.toCodePoint(current, value.charAt(++idx));
                bytes.write(0xf0 | (code >> 18));
                bytes.write(0x80 | ((code >> 12) & 0x3f));
                bytes.write(0x80 | ((code >> 6) & 0x3f));
                bytes.write(0x80 | (code & 0x3f));
            } else if (current < 0x80) {
                bytes.write(current);
            } else if (current < 0x800) {
                bytes.write(0xc0 | (current >> 6));
                bytes.write(0x80 | (current & 0x3f));
            } else {
                bytes.write(0xe0 | (current >> 12));
                bytes.write(0x80 | ((current >> 6) & 0x3f));
                bytes.write(0x80 | (current & 0x3f));
            }
        }
        return bytes.toByteArray();
    }

    /**
     * Decode UTF-8 bytes while retaining encoded surrogate code units.
     * @param bytes Bytes to decode
     * @return Decoded string
     */
    private static String stringValue(final byte[] bytes) {
        final StringBuilder value = new StringBuilder(bytes.length);
        for (int idx = 0; idx < bytes.length;) {
            final int first = bytes[idx] & 0xff;
            if (first < 0x80) {
                value.append((char) first);
                ++idx;
            } else if (first < 0xe0 && idx + 1 < bytes.length
                && JavaCodec.continuation(bytes[idx + 1])) {
                value.append((char) (((first & 0x1f) << 6) | (bytes[idx + 1] & 0x3f)));
                idx += 2;
            } else if (first < 0xf0 && idx + 2 < bytes.length
                && JavaCodec.continuation(bytes[idx + 1])
                && JavaCodec.continuation(bytes[idx + 2])) {
                value.append((char) (((first & 0x0f) << 12)
                    | ((bytes[idx + 1] & 0x3f) << 6) | (bytes[idx + 2] & 0x3f)));
                idx += 3;
            } else if (first < 0xf8 && idx + 3 < bytes.length
                && JavaCodec.continuation(bytes[idx + 1])
                && JavaCodec.continuation(bytes[idx + 2])
                && JavaCodec.continuation(bytes[idx + 3])) {
                final int code = ((first & 0x07) << 18)
                    | ((bytes[idx + 1] & 0x3f) << 12)
                    | ((bytes[idx + 2] & 0x3f) << 6) | (bytes[idx + 3] & 0x3f);
                value.appendCodePoint(code);
                idx += 4;
            } else {
                value.append('\ufffd');
                ++idx;
            }
        }
        return value.toString();
    }

    /**
     * Check a UTF-8 continuation byte.
     * @param value Byte
     * @return Whether it is a continuation byte
     */
    private static boolean continuation(final byte value) {
        return (value & 0xc0) == 0x80;
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
}
