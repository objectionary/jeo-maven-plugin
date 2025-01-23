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

/**
 * EO codec.
 * Converts primitive types to byte arrays and vice versa.
 * @since 0.8
 * @checkstyle CyclomaticComplexityCheck (500 lines)
 */
public final class EoCodec implements Codec {

    /**
     * Origin codec.
     */
    private final Codec origin;

    /**
     * Constructor.
     */
    public EoCodec() {
        this(new JavaCodec());
    }

    /**
     * Constructor.
     * @param delegate Origin codec.
     */
    private EoCodec(final Codec delegate) {
        this.origin = delegate;
    }

    @Override
    public byte[] encode(final Object object, final DataType type) {
        final byte[] result;
        switch (type) {
            case BOOL:
            case CHAR:
            case STRING:
            case BYTES:
            case LABEL:
            case TYPE_REFERENCE:
            case CLASS_REFERENCE:
            case NULL:
                result = this.origin.encode(object, type);
                break;
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                result = ByteBuffer.allocate(Double.BYTES)
                    .putDouble(((Number) object).doubleValue())
                    .array();
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
            case CHAR:
            case STRING:
            case BYTES:
            case LABEL:
            case TYPE_REFERENCE:
            case CLASS_REFERENCE:
            case NULL:
                result = this.origin.decode(bytes, type);
                break;
            case BYTE:
                result = (byte) ByteBuffer.wrap(bytes).getDouble();
                break;
            case SHORT:
                result = (short) ByteBuffer.wrap(bytes).getDouble();
                break;
            case INT:
                result = (int) ByteBuffer.wrap(bytes).getDouble();
                break;
            case LONG:
                result = (long) ByteBuffer.wrap(bytes).getDouble();
                break;
            case FLOAT:
                result = (float) ByteBuffer.wrap(bytes).getDouble();
                break;
            case DOUBLE:
                result = ByteBuffer.wrap(bytes).getDouble();
                break;
            default:
                throw new UnsupportedDataType(type);
        }
        return result;
    }
}
