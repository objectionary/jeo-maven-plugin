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
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.eolang.jeo.representation.bytecode.BytecodeValue;
import org.xembly.Directive;
import org.xembly.Directives;

public final class DirectivesNumberBytes implements Iterable<Directive> {

    /**
     * Array of hexadecimal characters.
     * Used for converting bytes to hexadecimal.
     * See {@link #bytesToHex(byte[])}.
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private final Number number;

    public DirectivesNumberBytes(final Number number) {
        this.number = number;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .add("o")
            .attr("base", "org.eolang.number")
            .append(new DirectivesBytes(this.bytes()))
            .up()
            .iterator();
    }

    private String bytes() {
        return DirectivesNumberBytes.bytesToHex(
            new BytecodeValue(this.number.doubleValue()).bytes());
    }

    /**
     * @todo Duplicate!
     */
    private static String bytesToHex(final byte[] bytes) {
        final String res;
        if (bytes == null || bytes.length == 0) {
            res = "--";
        } else {
            final int length = bytes.length;
            final char[] hex = new char[length * 3];
            for (int index = 0; index < length; ++index) {
                final int value = bytes[index] & 0xFF;
                hex[index * 3] = HEX_ARRAY[value >>> 4];
                hex[index * 3 + 1] = HEX_ARRAY[value & 0x0F];
                hex[index * 3 + 2] = '-';
            }
            if (hex.length == 3) {
                res = new String(hex);
            } else {
                res = new String(hex, 0, hex.length - 1);
            }
        }
        return res;
    }
}
