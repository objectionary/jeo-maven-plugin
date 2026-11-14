/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongUnaryOperator;
import java.util.stream.Stream;
import org.eolang.jeo.representation.Counter;

/**
 * Translation summary log.
 *
 * <p>This class wraps a translator and provides summary logging functionality. It logs the start
 * and end of the translation process, including the number of files processed and the total
 * time taken.</p>
 *
 * @since 0.2.0
 */
public final class Summary implements Translator {

    /**
     * Process name.
     */
    private final String process;

    /**
     * Past participle of the process.
     *
     * <p>Usually it is something like: "disassembled", "assembled", etc.</p>
     */
    private final String participle;

    /**
     * From where.
     */
    private final String input;

    /**
     * To where.
     */
    private final Path output;

    /**
     * Original translator.
     */
    private final Translator original;

    /**
     * Function returning how many files came from the cache given the total count.
     */
    private final LongUnaryOperator cached;

    /**
     * Constructor.
     *
     * @param process Process name (gerund form)
     * @param participle Past participle of the process
     * @param input Source directory path
     * @param output Target directory path
     * @param original Original translator to wrap with summary logging
     */
    Summary(
        final String process,
        final String participle,
        final String input,
        final Path output,
        final Translator original
    ) {
        this(process, participle, input, output, original, total -> 0L);
    }

    /**
     * Constructor.
     *
     * @param process Process name (gerund form)
     * @param participle Past participle of the process
     * @param input Source directory path
     * @param output Target directory path
     * @param original Original translator to wrap with summary logging
     * @param transformed Counter of files that were actually transformed
     */
    Summary(
        final String process,
        final String participle,
        final String input,
        final Path output,
        final Translator original,
        final Counter transformed
    ) {
        this(
            process,
            participle,
            input,
            output,
            original,
            total -> Math.max(0L, total - transformed.count())
        );
    }

    /**
     * Constructor.
     *
     * @param process Process name (gerund form)
     * @param participle Past participle of the process
     * @param input Source directory path
     * @param output Target directory path
     * @param original Original translator to wrap with summary logging
     * @param cached Function computing cached count from total count
     */
    private Summary(
        final String process,
        final String participle,
        final String input,
        final Path output,
        final Translator original,
        final LongUnaryOperator cached
    ) {
        this.process = process;
        this.participle = participle;
        this.input = input;
        this.output = output;
        this.original = original;
        this.cached = cached;
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
            .peek(rep -> counter.incrementAndGet()).onClose(
                () -> this.report(counter.get(), System.currentTimeMillis() - start)
            );
    }

    private void report(final long total, final long elapsed) {
        final long reused = this.cached.applyAsLong(total);
        final long fresh = total - reused;
        if (reused == 0L) {
            Logger.info(
                this,
                "Total %d files were %s in %[ms]s",
                fresh,
                this.participle,
                elapsed
            );
        } else {
            Logger.info(
                this,
                "Total %d files were %s and %d were taken from the cache in %[ms]s",
                fresh,
                this.participle,
                reused,
                elapsed
            );
        }
    }
}
