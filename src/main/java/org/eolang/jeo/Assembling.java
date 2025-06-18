/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.XmirRepresentation;

/**
 * Assembling transformation.
 *
 * <p>This class implements the transformation process that converts XMIR
 * representations into Java bytecode. It reads XMIR files and produces
 * corresponding .class files in the specified target directory.</p>
 * @since 0.6.0
 */
public final class Assembling implements Transformation {

    /**
     * Target folder where to save the assembled class.
     */
    private final Path folder;

    /**
     * Representation to assemble.
     */
    private final Path from;

    /**
     * XMIR representation.
     */
    private final XmirRepresentation repr;

    /**
     * Constructor.
     * @param target Target folder where the assembled class will be saved
     * @param representation Path to the XMIR representation to assemble
     */
    Assembling(final Path target, final Path representation) {
        this.folder = target;
        this.from = representation;
        this.repr = new XmirRepresentation(this.from);
    }

    @Override
    public Path source() {
        return this.from;
    }

    @Override
    public Path target() {
        final String name = new PrefixedName(this.repr.name()).decode();
        final String[] subpath = name.split("\\.");
        subpath[subpath.length - 1] = String.format("%s.class", subpath[subpath.length - 1]);
        return Paths.get(this.folder.toString(), subpath);
    }

    @Override
    public byte[] transform() {
        return this.repr.toBytecode().bytes();
    }
}
