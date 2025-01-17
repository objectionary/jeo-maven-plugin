/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Translation that leaves a log message before and after applying the original translation.
 * @since 0.6
 */
public final class Logging implements Transformation {

    /**
     * Process name.
     * Usually it is a noun, like:
     * "Disassembling", "Assembling", etc.
     */
    private final String process;

    /**
     * Participle of the process.
     * Something like:
     * "disassembled", "assembled", etc.
     */
    private final String participle;

    /**
     * Original transformation.
     */
    private final Transformation origin;

    /**
     * Constructor.
     * @param process Process name.
     * @param participle Participle of the process.
     * @param origin Original transformation.
     */
    public Logging(final String process, final String participle, final Transformation origin) {
        this.process = process;
        this.participle = participle;
        this.origin = origin;
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
     * @param source Initial path.
     */
    private void logStartWithSize(final Path source) {
        Logger.info(
            this,
            "%s '%[file]s' (%[size]s)",
            this.process,
            source,
            Logging.size(source)
        );
    }

    /**
     * Log the end of the process.
     * @param source Initial path
     * @param after Path after the process
     * @param time Time spent
     */
    private void logEndWithSize(final Path source, final Path after, final long time) {
        Logger.info(
            this,
            "'%[file]s' %s to '%[file]s' (%[size]s) in %[ms]s",
            source,
            this.participle,
            after,
            Logging.size(after),
            time
        );
    }

    /**
     * Size of the file.
     * @param path Path to the file
     * @return Size of the file
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
                String.format("Can't determine size of '%s'", path),
                exception
            );
        }
    }
}
