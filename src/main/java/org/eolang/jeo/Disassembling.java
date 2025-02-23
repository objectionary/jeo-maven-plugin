/*
 * The MIT License (MIT)
 *
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
 * @since 0.6
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
     * Constructor.
     * @param target Target folder.
     * @param representation Representation to disassemble.
     * @param mode Disassemble mode.
     */
    Disassembling(final Path target, final Path representation, final DisassembleMode mode) {
        this.folder = target;
        this.from = representation;
        this.mode = mode;
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
            .toEO(this.mode)
            .toString()
            .getBytes(StandardCharsets.UTF_8);
    }
}
