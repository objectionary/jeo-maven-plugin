/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
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
     * XMIR file extension pattern.
     */
    private static final Pattern XMIR = Pattern.compile(".xmir", Pattern.LITERAL);

    /**
     * Source folder where all the XMIR representations are located.
     */
    private final Path from;

    /**
     * Target folder where to save the assembled class.
     */
    private final Path tgt;

    /**
     * Representation to assemble.
     */
    private final Path xmir;

    /**
     * Constructor.
     * @param source Source folder where all the XMIR representations are located
     * @param target Target folder where the assembled class will be saved
     * @param representation Path to the XMIR representation to assemble
     */
    Assembling(final Path source, final Path target, final Path representation) {
        this.from = source;
        this.tgt = target;
        this.xmir = representation;
    }

    @Override
    public Path source() {
        return this.xmir;
    }

    @Override
    public Path target() {
        return Paths.get(
            this.tgt.toString(),
            Assembling.XMIR.matcher(this.from.relativize(this.xmir).toString()).replaceAll(".class")
        );
    }

    @Override
    public byte[] transform() {
        return new XmirRepresentation(this.xmir).toBytecode().bytes();
    }
}
