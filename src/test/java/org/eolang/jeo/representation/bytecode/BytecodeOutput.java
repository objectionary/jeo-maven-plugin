/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * Bytecode of compiled class.
 *
 * @since 0.1.0
 */
final class BytecodeOutput extends SimpleJavaFileObject {

    /**
     * Output stream.
     */
    private final ByteArrayOutputStream output;

    /**
     * Constructor.
     *
     * @param name Name of Java class
     */
    BytecodeOutput(final String name) {
        this(URI.create(String.format("%s%s", name, Kind.CLASS.extension)));
    }

    /**
     * Constructor.
     *
     * @param uri URI of Java class
     */
    private BytecodeOutput(final URI uri) {
        super(uri, Kind.CLASS);
        this.output = new ByteArrayOutputStream();
    }

    @Override
    public OutputStream openOutputStream() {
        return this.output;
    }

    /**
     * Get the bytecode.
     *
     * @return Bytecode
     */
    byte[] bytecode() {
        return this.output.toByteArray();
    }
}
