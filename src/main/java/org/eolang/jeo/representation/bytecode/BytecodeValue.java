/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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

/**
 * Bytecode value.
 * Represents a typed value in bytecode format.
 * @since 0.6
 */
public final class BytecodeValue {

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
     * @param value Value.
     */
    public BytecodeValue(final Object value) {
        this(DataType.findByData(value), value);
    }

    /**
     * Constructor.
     * @param type Value type.
     * @param bytes Value bytes.
     */
    public BytecodeValue(final String type, final byte[] bytes) {
        this(DataType.findByBase(type), bytes);
    }

    /**
     * Constructor.
     * @param type Value type.
     * @param value Value.
     */
    private BytecodeValue(final DataType type, final Object value) {
        this(type, type.encode(value));
    }

    /**
     * Constructor.
     * @param type Value type.
     * @param bytes Value bytes.
     */
    private BytecodeValue(final DataType type, final byte[] bytes) {
        this.vtype = type;
        this.vbytes = bytes;
    }

    /**
     * Represent the value as an object.
     * @return Object.
     */
    public Object object() {
        return this.vtype.decode(this.vbytes);
    }

    /**
     * Retrieve the type of the value.
     * @return Type.
     */
    public String type() {
        return this.vtype.caption().toLowerCase(Locale.ROOT);
    }

    /**
     * Retrieve the bytes of the value.
     * @return Bytes.
     */
    public byte[] bytes() {
        final byte[] result;
        if (this.vbytes == null) {
            result = null;
        } else {
            result = this.vbytes.clone();
        }
        return result;
    }
}
