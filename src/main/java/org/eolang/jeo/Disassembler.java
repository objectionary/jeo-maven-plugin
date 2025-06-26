/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.eolang.jeo.representation.asm.DisassembleMode;

/**
 * Disassembler for bytecode classes.
 *
 * <p>This class disassembles the project's compiled Java bytecode classes into
 * XMIR (EO XML representation). It processes all .class files from a specified
 * directory and converts them into corresponding XMIR files, supporting different
 * disassembly modes for various levels of detail.</p>
 * @since 0.1.0
 */
public class Disassembler {

    /**
     * Project compiled classes.
     */
    private final Path classes;

    /**
     * Where to save decompiled classes.
     */
    private final Path target;

    /**
     * Disassemble mode.
     */
    private final DisassembleMode mode;

    /**
     * Whether to omit detailed listings in XMIR output.
     */
    private final boolean omitListings;

    /**
     * Constructor.
     * @param classes Directory containing compiled class files
     * @param target Target directory where XMIR files will be saved
     */
    public Disassembler(
        final Path classes,
        final Path target
    ) {
        this(classes, target, DisassembleMode.SHORT, true);
    }

    /**
     * Constructor.
     * @param classes Directory containing compiled class files
     * @param target Target directory where XMIR files will be saved
     * @param mode Disassemble mode controlling the level of detail
     */
    public Disassembler(
        final Path classes,
        final Path target,
        final DisassembleMode mode
    ) {
        this(classes, target, mode, true);
    }

    /**
     * Constructor.
     * @param classes Directory containing compiled class files
     * @param target Target directory where XMIR files will be saved
     * @param mode Disassemble mode controlling the level of detail
     * @param omitListings Whether to omit detailed listings in XMIR output
     */
    public Disassembler(
        final Path classes,
        final Path target,
        final DisassembleMode mode,
        final boolean omitListings
    ) {
        this.classes = classes;
        this.target = target;
        this.mode = mode;
        this.omitListings = omitListings;
    }

    /**
     * Disassemble all bytecode files.
     */
    public void disassemble() {
        final String process = "Disassembling";
        final String disassembled = "disassembled";
        final Stream<Path> stream = new Summary(
            process,
            disassembled,
            this.classes,
            this.target,
            new ParallelTranslator(this::disassemble)
        ).apply(new BytecodeClasses(this.classes).all());
        stream.forEach(this::log);
        stream.close();
    }

    /**
     * Disassemble a single bytecode file.
     * @param path Path to the bytecode file to disassemble
     * @return Path to the disassembled XMIR file
     */
    private Path disassemble(final Path path) {
        final Transformation trans = new Logging(
            "Disassembling",
            "disassembled",
            new Caching(
                new Disassembling(this.target, path, this.mode, this.omitListings)
            )
        );
        trans.transform();
        return trans.target();
    }

    /**
     * Log the disassembling process.
     * @param disassembled Path to the disassembled XMIR file
     */
    private void log(final Path disassembled) {
        try {
            Logger.debug(
                this,
                "Disassembling of %[file]s (%[size]s) finished successfully",
                disassembled,
                Files.size(disassembled)
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to get size of '%s'",
                    disassembled
                ),
                exception
            );
        }
    }
}
