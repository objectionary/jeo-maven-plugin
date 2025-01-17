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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Java bytecode.
 * @since 0.1.0
 */
public final class Bytecode {

    /**
     * Bytecode Bytes.
     */
    private final byte[] codes;

    /**
     * Constructor.
     * @param bytes Bytecode bytes.
     */
    public Bytecode(final byte[] bytes) {
        this.codes = Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Get as bytes.
     * @return Bytecode bytes.
     */
    public byte[] bytes() {
        return Arrays.copyOf(this.codes, this.codes.length);
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other == null || this.getClass() != other.getClass()) {
            result = false;
        } else {
            final Bytecode bytecode = (Bytecode) other;
            result = Arrays.equals(this.codes, bytecode.codes);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.codes);
    }

    @Override
    public String toString() {
        final StringWriter out = new StringWriter();
        new ClassReader(this.codes)
            .accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(out)), 0);
        return out.toString();
    }
}
