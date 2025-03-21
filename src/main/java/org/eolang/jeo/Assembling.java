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
 * @since 0.6
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
     * @param target Target folder.
     * @param representation Representation to assemble.
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
