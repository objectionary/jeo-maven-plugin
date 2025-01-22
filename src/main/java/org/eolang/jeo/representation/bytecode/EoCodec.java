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

public final class EoCodec implements Codec {

    /**
     * Maximum long value that can be represented as double.
     * Any value greater than this will be represented incorrectly.
     */
    private static final long MAX_LONG_DOUBLE = 9_007_199_254_740_992L;

    /**
     * Minimum long value that can be represented as double.
     * Any value less than this will be represented incorrectly.
     */
    private static final long MIN_LONG_DOUBLE = -9_007_199_254_740_992L;

    /**
     * Origin codec.
     */
    private final Codec origin;

    public EoCodec() {
        this(new PlainCodec());
    }

    private EoCodec(final Codec origin) {
        this.origin = origin;
    }

    @Override
    public byte[] bytes(final Object object, final DataType type) {
        switch (type) {
            case BOOL:
            case CHAR:
            case STRING:
            case BYTES:
            case LABEL:
            case TYPE_REFERENCE:
            case CLASS_REFERENCE:
            case NULL:
                return this.origin.bytes(object, type);
            case BYTE:
            case SHORT:
            case INT:
            case FLOAT:
            case DOUBLE:
                return ByteBuffer.allocate(Double.BYTES).putDouble((double) object).array();
            case LONG:
                if (EoCodec.MIN_LONG_DOUBLE <= (long) object && (long) object <= EoCodec.MAX_LONG_DOUBLE) {
                    return this.origin.bytes(object, type);
                } else {
                    return ByteBuffer.allocate(Long.BYTES).putLong((long) object).array();
                }
            default:
                throw new IllegalArgumentException(
                    String.format("Unsupported data type: %s", type)
                );
        }
    }

    @Override
    public Object object(final byte[] bytes, final DataType type) {
        switch (type) {
            case BOOL:
            case CHAR:
            case STRING:
            case BYTES:
            case LABEL:
            case TYPE_REFERENCE:
            case CLASS_REFERENCE:
            case NULL:
                return this.origin.object(bytes, type);
            case BYTE:
                return (byte) ByteBuffer.wrap(bytes).getDouble();
            case SHORT:
                return (short) ByteBuffer.wrap(bytes).getDouble();
            case INT:
                return (int) ByteBuffer.wrap(bytes).getDouble();
            case LONG:
                return (long) ByteBuffer.wrap(bytes).getDouble();
            case FLOAT:
                return (float) ByteBuffer.wrap(bytes).getDouble();
            case DOUBLE:
                return ByteBuffer.wrap(bytes).getDouble();
            default:
                throw new IllegalArgumentException(
                    String.format("Unsupported data type: %s", type)
                );
        }
    }
}
