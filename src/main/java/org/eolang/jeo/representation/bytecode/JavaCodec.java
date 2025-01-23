/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
import java.util.Optional;
import org.objectweb.asm.Type;

/**
 * Plain codec.
 * Converts objects to bytes and vice versa using Java type sizes.
 * @since 0.8
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
        switch (type) {
            case BOOL:
                return Boolean.valueOf(bytes[0] != 0);
            case CHAR:
                return ByteBuffer.wrap(bytes).getChar();
            case BYTE:
                return ByteBuffer.wrap(bytes).get();
            case SHORT:
                return ByteBuffer.wrap(bytes).getShort();
            case INT:
                return (int) ByteBuffer.wrap(bytes).getLong();
            case LONG:
                return ByteBuffer.wrap(bytes).getLong();
            case FLOAT:
                return ByteBuffer.wrap(bytes).getFloat();
            case DOUBLE:
                return ByteBuffer.wrap(bytes).getDouble();
            case STRING:
                return Optional.ofNullable(bytes)
                    .map(all -> new String(all, StandardCharsets.UTF_8))
                    .orElse("");
            case BYTES:
                return bytes;
            case LABEL:
                return new BytecodeLabel(new String(bytes, StandardCharsets.UTF_8));
            case TYPE_REFERENCE:
                return Type.getType(String.format(new String(bytes, StandardCharsets.UTF_8)));
            case CLASS_REFERENCE:
                return new String(bytes, StandardCharsets.UTF_8);
            case NULL:
                return null;
            default:
                throw new UnsupportedDataType(type);
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
