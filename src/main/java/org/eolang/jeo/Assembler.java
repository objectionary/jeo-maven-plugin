/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.eolang.jeo.representation.Counter;

/**
 * Assembler.
 *
 * <p>This class is responsible for assembling the project's XMIR (EO XML representation) source
 * files into Java bytecode (.class files). It processes all XMIR files from an input directory
 * and generates corresponding bytecode files in an output directory.</p>
 *
 * @since 0.2.0
 */
public final class Assembler {

    /**
     * Input folder with "xmir" files.
     */
    private final Path input;

    /**
     * Output folder for the assembled classes.
     */
    private final Path output;

    /**
     * Enables detailed debug logging.
     */
    private final boolean debug;

    /**
     * Number of threads for parallel processing.
     *
     * <p>When 0, the number of available processors is used automatically.</p>
     */
    private final int threads;

    /**
     * Constructor.
     *
     * @param input Input folder with "xmir" files
     * @param output Output folder for the assembled classes
     * @param debug Enables detailed debug logging
     */
    public Assembler(final Path input, final Path output, final boolean debug) {
        this(input, output, debug, 0);
    }

    /**
     * Constructor.
     *
     * @param input Input folder with "xmir" files
     * @param output Output folder for the assembled classes
     * @param debug Enables detailed debug logging
     * @param threads Number of threads (0 = use available processors automatically)
     */
    public Assembler(
        final Path input, final Path output, final boolean debug, final int threads
    ) {
        this.input = input;
        this.output = output;
        this.debug = debug;
        this.threads = threads;
    }

    /**
     * Assemble all XMIR files.
     *
     * @since 0.2.0
     */
    public void assemble() {
        final XmirFiles files = new XmirFiles(this.input);
        final Counter counter = new Counter(files.total());
        final Stream<Path> all = new Summary(
            "Assembling",
            "assembled",
            this.input.toString(),
            this.output,
            new ParallelTranslator(path -> this.assemble(path, counter), this.threads)
        ).apply(files.all());
        all.forEach(this::log);
        all.close();
    }

    private Path assemble(final Path path, final Counter counter) {
        final Transformation trans = new Caching(
            new Logging(
                "Assembling",
                "assembled",
                new Informative(new Assembling(this.input, this.output, path)),
                this.debug,
                counter
            )
        );
        trans.transform();
        return trans.target();
    }

    private void log(final Path disassembled) {
        try {
            Logger.debug(
                this,
                "Assembling of %[file]s (%[size]s) finished successfully",
                disassembled,
                Files.size(disassembled)
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't get size of '%s'", disassembled),
                exception
            );
        }
    }
}
