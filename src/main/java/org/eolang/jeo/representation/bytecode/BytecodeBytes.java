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

import java.util.Locale;
import java.util.Optional;

/**
 * Bytecode byte array.
 * @since 0.8
 */
public final class BytecodeBytes {

    /**
     * Data type.
     */
    private final DataType vtype;

    /**
     * Bytes.
     */
    private final byte[] vbytes;

    /**
     * Constructor.
     * @param type Value type.
     * @param bytes Value bytes.
     */
    public BytecodeBytes(final String type, final byte[] bytes) {
        this(DataType.findByBase(type), bytes);
    }

    /**
     * Constructor.
     * @param type Value type.
     * @param bytes Value bytes.
     */
    private BytecodeBytes(final DataType type, final byte[] bytes) {
        this.vtype = type;
        this.vbytes = Optional.ofNullable(bytes).map(byte[]::clone).orElse(null);
    }

    /**
     * Represent the value as an object.
     * @param codec Codec.
     * @return Object.
     */
    public Object object(final Codec codec) {
        return codec.decode(this.vbytes, this.vtype);
    }

    /**
     * Retrieve the type of the value.
     * @return Type.
     */
    public String type() {
        return this.vtype.caption().toLowerCase(Locale.ROOT);
    }

}
