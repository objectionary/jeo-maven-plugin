/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Translation summary log.
 *
 * <p>This class wraps a translator and provides summary logging functionality.
 * It logs the start and end of the translation process, including the number of
 * files processed and the total time taken.</p>
 * @since 0.2.0
 */
public final class Summary implements Translator {

    /**
     * Process name.
     */
    private final String process;

    /**
     * Past participle of the process.
     * <p>Usually it is something like:
     * "disassembled", "assembled", etc.</p>
     */
    private final String participle;

    /**
     * From where.
     */
    private final Path input;

    /**
     * To where.
     */
    private final Path output;

    /**
     * Original translator.
     */
    private final Translator original;

    /**
     * Constructor.
     * @param process Process name (gerund form)
     * @param participle Past participle of the process
     * @param input Source directory path
     * @param output Target directory path
     * @param original Original translator to wrap with summary logging
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    Summary(
        final String process,
        final String participle,
        final Path input,
        final Path output,
        final Translator original
    ) {
        this.process = process;
        this.participle = participle;
        this.input = input;
        this.output = output;
        this.original = original;
    }

    @Override
    public Stream<Path> apply(final Stream<Path> representations) {
        Logger.info(
            this,
            "%s files from %[file]s to %[file]s",
            this.process,
            this.input,
            this.output
        );
        final long start = System.currentTimeMillis();
        final AtomicInteger counter = new AtomicInteger();
        return this.original.apply(representations)
            .peek(rep -> counter.incrementAndGet())
            .onClose(
                () -> Logger.info(
                    this,
                    "Total %d files were %s in %[ms]s",
                    counter.get(),
                    this.participle,
                    System.currentTimeMillis() - start
                )
            );
    }
}
