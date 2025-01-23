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

public final class PlainCodec implements Codec {
    private static final byte[] EMPTY = new byte[0];

    @Override
    public byte[] encode(final Object value, final DataType type) {
        switch (type) {
            case BOOL:
                final byte[] result;
                if (value instanceof Integer) {
                    result = PlainCodec.hexBoolean((int) value != 0);
                } else {
                    result = PlainCodec.hexBoolean(Boolean.class.cast(value));
                }
                return result;
            case CHAR:
                final char val;
                if (value instanceof Integer) {
                    val = (char) (int) value;
                } else {
                    val = (char) value;
                }
                return ByteBuffer.allocate(Character.BYTES).putChar(val).array();
            case BYTE:
                return ByteBuffer.allocate(Byte.BYTES).put((byte) value).array();
            case SHORT:
                return ByteBuffer.allocate(Short.BYTES).putShort((short) value).array();
            case INT:
                return ByteBuffer.allocate(Long.BYTES).putLong((int) value).array();
            case LONG:
                return ByteBuffer.allocate(Long.BYTES).putLong((long) value).array();
            case FLOAT:
                return ByteBuffer.allocate(Float.BYTES).putFloat((float) value).array();
            case DOUBLE:
                return ByteBuffer.allocate(Double.BYTES).putDouble((double) value).array();
            case STRING:
                return Optional.ofNullable(value).map(String::valueOf)
                    .map(unicode -> unicode.getBytes(StandardCharsets.UTF_8))
                    .orElse(null);
            case BYTES:
                return byte[].class.cast(value);
            case LABEL:
                return BytecodeLabel.class.cast(value).uid().getBytes(StandardCharsets.UTF_8);
            case TYPE_REFERENCE:
                return PlainCodec.typeBytes(value);
            case CLASS_REFERENCE:
                return PlainCodec.hexClass(Class.class.cast(value).getName());
            case NULL:
                return PlainCodec.EMPTY;
            default:
                throw new IllegalArgumentException(
                    String.format("Unsupported data type: %s", type)
                );
        }
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
                throw new IllegalArgumentException(
                    String.format("Unsupported data type: %s", type)
                );
        }
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
            return PlainCodec.hexClass(((Type) value).getDescriptor());
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
