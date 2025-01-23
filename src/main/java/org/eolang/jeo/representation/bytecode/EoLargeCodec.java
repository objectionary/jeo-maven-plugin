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

public final class EoLargeCodec implements Codec {

    private final Codec origin;

    public EoLargeCodec() {
        this(new EoCodec());
    }

    public EoLargeCodec(final Codec origin) {
        this.origin = origin;
    }

    @Override
    public byte[] encode(final Object object, final DataType type) {
        if (type == DataType.LONG) {
            return ByteBuffer.allocate(Long.BYTES).putLong((long) object).array();
        } else {
            return this.origin.encode(object, type);
        }
    }

    @Override
    public Object decode(final byte[] bytes, final DataType type) {
        if (type == DataType.LONG) {
            return ByteBuffer.wrap(bytes).getLong();
        } else {
            return this.origin.decode(bytes, type);
        }
    }
}
