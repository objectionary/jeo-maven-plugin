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
import org.eolang.jeo.representation.asm.DisassembleParams;

/**
 * Disassembler for bytecode classes.
 *
 * <p>This class disassembles the project's compiled Java bytecode classes into
 * XMIR (EO XML representation). It processes all .class files from a specified
 * directory and converts them into corresponding XMIR files, supporting different
 * disassembly modes for various levels of detail.</p>
 * @since 0.1.0
 */
public final class Disassembler {

    /**
     * Project compiled classes.
     */
    private final Classes classes;

    /**
     * Where to save decompiled classes.
     */
    private final Path target;

    /**
     * Disassemble params.
     */
    private final DisassembleParams params;

    /**
     * Constructor.
     * @param classes Directory containing compiled class files
     * @param target Target directory where XMIR files will be saved
     */
    public Disassembler(
        final Path classes,
        final Path target
    ) {
        this(classes, target, new DisassembleParams());
    }

    /**
     * Constructor.
     * @param classes Directory containing compiled class files
     * @param target Target directory where XMIR files will be saved
     * @param params Disassembling params.
     */
    public Disassembler(
        final Path classes,
        final Path target,
        final DisassembleParams params
    ) {
        this(new BytecodeClasses(classes), target, params);
    }

    public Disassembler(
        final Classes classes,
        final Path target,
        final DisassembleParams params
    ) {
        this.classes = classes;
        this.target = target;
        this.params = params;
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
            this.classes.toString(),
            this.target,
            new ParallelTranslator(this::disassemble)
        ).apply(this.classes.all());
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
            new Caching(new Informative(new Disassembling(this.target, path, this.params)))
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
