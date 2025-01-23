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
 * Codec that saves long as a plain byte array.
 * The delegate codec encodes all the rest data types.
 * @since 0.8
 */
public final class PlainLongCodec implements Codec {

    /**
     * Delegate codec.
     */
    private final Codec origin;

    /**
     * Constructor.
     * @param delegate Origin codec.
     */
    public PlainLongCodec(final Codec delegate) {
        this.origin = delegate;
    }

    @Override
    public byte[] encode(final Object object, final DataType type) {
        final byte[] result;
        if (type == DataType.LONG) {
            result = ByteBuffer.allocate(Long.BYTES).putLong((long) object).array();
        } else {
            result = this.origin.encode(object, type);
        }
        return result;
    }

    @Override
    public Object decode(final byte[] bytes, final DataType type) {
        final Object result;
        if (type == DataType.LONG) {
            result = ByteBuffer.wrap(bytes).getLong();
        } else {
            result = this.origin.decode(bytes, type);
        }
        return result;
    }
}
