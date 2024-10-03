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

public final class BytecodeValue {

    private final DataType type;
    private final byte[] bytes;

    public BytecodeValue(final Object value) {
        this(DataType.type(value), DataType.toBytes(value));
    }

    public BytecodeValue(final String type, final byte[] bytes) {
        this(DataType.find(type), bytes);
    }

    public BytecodeValue(final DataType type, final byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }

    public Object asObject() {
        return this.type.decode(this.bytes);
    }

    public String type() {
        return this.type.base().toLowerCase(Locale.ROOT);
    }

    public byte[] bytes() {
        return this.bytes;
    }
}
