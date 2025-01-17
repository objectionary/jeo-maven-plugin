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
import lombok.ToString;
import org.eolang.jeo.representation.bytecode.BytecodeValue;
import org.xembly.Directive;

/**
 * Data Object Directive in EO language.
 *
 * @since 0.1.0
 */
@ToString
public final class DirectivesValue implements Iterable<Directive> {

    /**
     * Array of hexadecimal characters.
     * Used for converting bytes to hexadecimal.
     * See {@link #bytesToHex(byte[])}.
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Name.
     */
    private final String name;

    /**
     * Value.
     */
    private final BytecodeValue value;

    /**
     * Constructor.
     * @param data Data.
     * @param <T> Data type.
     */
    public <T> DirectivesValue(final T data) {
        this("", data);
    }

    /**
     * Constructor.
     * @param name Name.
     * @param data Data.
     * @param <T> Data type.
     */
    public <T> DirectivesValue(final String name, final T data) {
        this(name, new BytecodeValue(data));
    }

    /**
     * Constructor.
     * @param name Name.
     * @param value Value.
     */
    public DirectivesValue(final String name, final BytecodeValue value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String type = this.type();
        final Iterable<Directive> res;
        if ("string".equals(type)) {
            res = new DirectivesEoObject(
                type,
                this.name,
                new DirectivesComment(this.comment()),
                new DirectivesBytes(this.hex())
            );
        } else {
            res = new DirectivesJeoObject(
                type,
                this.name,
                new DirectivesComment(this.comment()),
                new DirectivesBytes(this.hex())
            );
        }
        return res.iterator();
    }

    /**
     * Value of the data.
     * @return Value
     */
    String hex() {
        return DirectivesValue.bytesToHex(this.value.bytes());
    }

    /**
     * Type of the data.
     * @return Type
     */
    String type() {
        return this.value.type();
    }

    /**
     * Bytes of the representative comment.
     * @return Sting comment.
     */
    private String comment() {
        final String result;
        final Object object = this.value.object();
        if (object instanceof String) {
            result = String.format("\"%s\"", object);
        } else {
            result = String.valueOf(object);
        }
        return result;
    }

    /**
     * Bytes to HEX.
     * The efficient way to convert bytes to hexadecimal.
     * ATTENTION!
     * Do not modify this method.
     * It is an optimized version that saves memory and CPU.
     * Actually, the solution is based on the following StackOverflow answer:
     * <a href="https://stackoverflow.com/a/9855338/10423604">here</a>
     * You can find the full explanation or any other examples there.
     *
     * @param bytes Bytes.
     * @return Hexadecimal value as string.
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
                hex[index * 3] = DirectivesValue.HEX_ARRAY[value >>> 4];
                hex[index * 3 + 1] = DirectivesValue.HEX_ARRAY[value & 0x0F];
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
