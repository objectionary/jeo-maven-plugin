/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Pretty-printing utility for Java bytecode.
 *
 * <p>This class provides human-readable representation of Java bytecode using ASM's
 * TraceClassVisitor. It converts raw bytecode into a textual format that shows
 * the structure and instructions of a class file.</p>
 * @since 0.6.0
 * @todo #845:90min Decompile Bytecode Listing.
 *  Currently we print bytecode listing to XMIR without decompiling it.
 *  It's just raw, but human-readable bytecode.
 *  We can make one step forward and try to decompile this code to Java.
 *  This would be a great improvement for readability of this listing.
 */
public final class BytecodeListing {

    /**
     * Raw bytecode.
     */
    private final byte[] bytecode;

    /**
     * Constructor.
     * @param bytecode The raw bytecode array to format
     */
    BytecodeListing(final byte... bytecode) {
        this.bytecode = bytecode.clone();
    }

    @Override
    public String toString() {
        final ClassReader reader = new ClassReader(this.bytecode);
        final StringWriter writer = new StringWriter();
        reader.accept(new TraceClassVisitor(new PrintWriter(writer)), 0);
        return writer.toString();
    }
}
