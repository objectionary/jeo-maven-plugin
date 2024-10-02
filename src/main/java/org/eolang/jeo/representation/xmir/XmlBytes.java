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
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.DataType;

/**
 * Xml representation of EO bytes.
 * @since 0.6
 * @todo #715:60min Refactor {@link XmlBytes} class.
 *  We should decide whether to keep this class or not.
 *  If we decide to keep it, we need to consider the combination of
 *  {@link XmlBytes#parse()}, {@link XmlBytes#text()} and {@link XmlBytes#hex()}
 *  methods into one method.
 */
public final class XmlBytes {

    /**
     * Data type.
     */
    private final DataType type;

    /**
     * Bytes.
     */
    private final XmlNode bytes;

    /**
     * Constructor.
     * @param base Base.
     * @param bytes Bytes.
     */
    XmlBytes(final String base, final XmlNode bytes) {
        this.type = DataType.find(base);
        this.bytes = bytes;
    }

    /**
     * Bytes text.
     * @return Bytes as string.
     */
    public String text() {
        return this.bytes.text();
    }

    /**
     * Parse bytes.
     * @return Parsed object.
     */
    public Object parse() {
        return this.type.decode(this.bytes.text());
    }

    /**
     * Convert to hex.
     * @return Hex string.
     */
    public HexString hex() {
        return new HexString(this.bytes.text());
    }
}
