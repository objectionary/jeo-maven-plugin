/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.asm.DisassembleMode;

/**
 * Disassembling transformation.
 *
 * <p>This class implements the transformation process that converts Java bytecode
 * into XMIR representation. It reads .class files and produces corresponding
 * XMIR files in the specified target directory with configurable detail levels.</p>
 * @since 0.6.0
 */
public final class Disassembling implements Transformation {

    /**
     * Target folder where to save the disassembled class.
     */
    private final Path folder;

    /**
     * Representation to disassemble.
     */
    private final Path from;

    /**
     * Disassemble mode.
     */
    private final DisassembleMode mode;

    /**
     * Whether to omit comments in generated XMIR files.
     */
    private final boolean omitComments;

    /**
     * Constructor.
     * @param target Target folder where the disassembled XMIR will be saved
     * @param representation Path to the bytecode representation to disassemble
     * @param mode Disassemble mode controlling the level of detail
     */
    Disassembling(final Path target, final Path representation, final DisassembleMode mode) {
        this(target, representation, mode, false);
    }

    /**
     * Constructor.
     * @param target Target folder where the disassembled XMIR will be saved
     * @param representation Path to the bytecode representation to disassemble
     * @param mode Disassemble mode controlling the level of detail
     * @param omitComments Whether to omit comments in generated XMIR files
     */
    Disassembling(final Path target, final Path representation, final DisassembleMode mode, final boolean omitComments) {
        this.folder = target;
        this.from = representation;
        this.mode = mode;
        this.omitComments = omitComments;
    }

    @Override
    public Path source() {
        return this.from;
    }

    @Override
    public Path target() {
        return this.folder.resolve(
            String.format(
                "%s.xmir",
                new PrefixedName(
                    new BytecodeRepresentation(this.from).name()
                ).decode().replace('/', File.separatorChar)
            )
        );
    }

    @Override
    public byte[] transform() {
        return new BytecodeRepresentation(this.from)
            .toEO(this.mode, this.omitComments)
            .toString()
            .getBytes(StandardCharsets.UTF_8);
    }
}
