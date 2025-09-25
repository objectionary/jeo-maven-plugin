/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eolang.jeo.representation.Counter;

/**
 * Logging transformation decorator.
 *
 * <p>This class wraps a transformation and adds logging functionality. It logs
 * messages before and after applying the original transformation, including file
 * sizes and processing time for performance monitoring.</p>
 * @since 0.6.0
 */
public final class Logging implements Transformation {

    /**
     * Process name.
     * <p>Usually it is a gerund form, like:
     * "Disassembling", "Assembling", etc.</p>
     */
    private final String process;

    /**
     * Participle of the process.
     * <p>Past participle form, like:
     * "disassembled", "assembled", etc.</p>
     */
    private final String participle;

    /**
     * Original transformation.
     */
    private final Transformation origin;

    /**
     * Enables detailed debug logging.
     */
    private final boolean debug;

    /**
     * File size counter.
     */
    private final Counter counter;

    /**
     * Constructor.
     * @param process Process name (gerund form)
     * @param participle Past participle of the process
     * @param origin Original transformation to wrap with logging
     * @param debug Enables detailed debug logging
     * @param counter File size counter
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public Logging(
        final String process,
        final String participle,
        final Transformation origin,
        final boolean debug,
        final Counter counter
    ) {
        this.process = process;
        this.participle = participle;
        this.origin = origin;
        this.debug = debug;
        this.counter = counter;
    }

    @Override
    public Path source() {
        return this.origin.source();
    }

    @Override
    public Path target() {
        return this.origin.target();
    }

    @Override
    public byte[] transform() {
        final byte[] result;
        this.logStartWithSize(this.source());
        final long start = System.currentTimeMillis();
        result = this.origin.transform();
        final long time = System.currentTimeMillis() - start;
        this.logEndWithSize(this.source(), this.target(), time);
        return result;
    }

    /**
     * Log the start of the process.
     * @param source Initial path of the file being processed
     */
    private void logStartWithSize(final Path source) {
        Logger.debug(
            this,
            "%s of %[file]s (%[size]s) started",
            this.process,
            source,
            Logging.size(source)
        );
    }

    /**
     * Log the end of the process.
     * @param source Initial path of the source file
     * @param after Path to the resulting file after processing
     * @param time Time spent in milliseconds
     */
    private void logEndWithSize(final Path source, final Path after, final long time) {
        synchronized (this.counter) {
            if (this.debug) {
                Logger.info(
                    this,
                    "%s %[file]s %s to %[file]s (%[size]s) in %[ms]s",
                    this.counter.next(),
                    source,
                    this.participle,
                    after,
                    Logging.size(after),
                    time
                );
            } else {
                Logger.info(
                    this,
                    "%s %[file]s (%[size]s) %s in %[ms]s",
                    this.counter.next(),
                    after.getFileName(),
                    Logging.size(after),
                    this.participle,
                    time
                );
            }
        }
    }

    /**
     * Size of the file.
     * @param path Path to the file
     * @return Size of the file in bytes, or 0 if file doesn't exist
     */
    private static long size(final Path path) {
        try {
            final long result;
            if (Files.exists(path)) {
                result = Files.size(path);
            } else {
                result = 0L;
            }
            return result;
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't determine the size of '%s'", path),
                exception
            );
        }
    }
}
