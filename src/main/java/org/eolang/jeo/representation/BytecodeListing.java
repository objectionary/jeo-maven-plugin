/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * This is pretty-printing of bytecode.
 * @since 0.6
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
     * @param bytecode Bytecode.
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
