/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
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
