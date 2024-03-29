/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.HexData;
import org.objectweb.asm.Type;

/**
 * Xmir representation of a bytecode array.
 *
 * @since 0.3
 */
public final class XmlTuple {

    /**
     * Xmir node.
     */
    private final XmlNode node;

    public XmlTuple(final String... lines) {
        this(String.join("\n", lines));
    }

    public XmlTuple(final String xml) {
        this(new XmlNode(xml));
    }

    /**
     * Constructor.
     * @param node Xmir node.
     */
    public XmlTuple(final XmlNode node) {
        this.node = node;
    }

    /**
     * Convert XML tuple to an object.
     * @return Object.
     */
    public Object asObject() {
        final Class<?> type = this.type();
        if (int[].class.equals(type)) {
            return this.toIntArray();
        } else if (byte[].class.equals(type)) {
            return this.toByteArray();
        } else if (char[].class.equals(type)) {
            return this.toCharArray();
        } else if (long[].class.equals(type)) {
            return this.toLongArray();
        } else if (float[].class.equals(type)) {
            return this.toFloatArray();
        } else if (double[].class.equals(type)) {
            return this.toDoubleArray();
        } else if (short[].class.equals(type)) {
            return this.toShortArray();
        } else if (boolean[].class.equals(type)) {
            return this.toBooleanArray();
        } else {
            return this.toObjectsArray();
        }
    }

    private Object[] toObjectsArray() {
        return this.elements().map(HexString::decode).toArray();
    }

    private boolean[] toBooleanArray() {
        final List<Boolean> values = this.elements().map(HexString::decodeAsBoolean)
            .collect(Collectors.toList());
        final boolean[] array = new boolean[values.size()];
        for (int idx = 0; idx < values.size(); ++idx) {
            array[idx] = values.get(idx);
        }
        return array;
    }

    private short[] toShortArray() {
        final int[] array = this.elements().mapToInt(HexString::decodeAsInt).toArray();
        final short[] shorts = new short[array.length];
        for (int idx = 0; idx < array.length; ++idx) {
            shorts[idx] = (short) array[idx];
        }
        return shorts;
    }

    private double[] toDoubleArray() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private float[] toFloatArray() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private long[] toLongArray() {
        return this.elements().mapToLong(HexString::decodeAsLong).toArray();
    }

    private char[] toCharArray() {
        final int[] array = this.elements().mapToInt(HexString::decodeAsInt).toArray();
        final char[] chars = new char[array.length];
        for (int idx = 0; idx < array.length; ++idx) {
            chars[idx] = (char) array[idx];
        }
        return chars;
    }

    private byte[] toByteArray() {
        final int[] array = elements().mapToInt(HexString::decodeAsInt).toArray();
        final byte[] bytes = new byte[array.length];
        for (int idx = 0; idx < array.length; ++idx) {
            bytes[idx] = (byte) array[idx];
        }
        return bytes;
    }

    private int[] toIntArray() {
        return this.elements().mapToInt(HexString::decodeAsInt).toArray();
    }

    private Stream<HexString> elements() {
        return this.node.children().skip(1)
            .map(XmlNode::text)
            .map(HexString::new);
    }

    private Class<?> type() {
        try {
            return Class.forName(new HexString(this.node.firstChild().text()).decode());
        } catch (final ClassNotFoundException exception) {
            throw new IllegalStateException(
                String.format(
                    "Can't parse type from XML tuple %n%s%n",
                    this.node
                ),
                exception
            );
        }
    }
}
