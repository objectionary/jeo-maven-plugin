/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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
import java.nio.file.Paths;

/**
 * Translation that leaves a log message before and after applying the original translation.
 * @since 0.2
 */
public final class LoggedTranslation implements Translation {

    /**
     * Unknown path.
     */
    private static final Path UNKNOWN = Paths.get("Unknown");

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
     * Original translation.
     */
    private final Translation original;

    /**
     * Constructor.
     * @param process Process name.
     * @param participle Participle of the process.
     * @param original Original translation.
     */
    public LoggedTranslation(
        final String process,
        final String participle,
        final Translation original
    ) {
        this.process = process;
        this.participle = participle;
        this.original = original;
    }

    @Override
    public Representation apply(final Representation representation) {
        final Representation result;
        final Path source = representation.details().source().orElse(LoggedTranslation.UNKNOWN);
        this.logStartWithSize(source);
        final long start = System.currentTimeMillis();
        result = this.original.apply(representation);
        final long time = System.currentTimeMillis() - start;
        final Path after = result.details().source().orElse(LoggedTranslation.UNKNOWN);
        this.logEndWithSize(source, after, time);
        return result;
    }

    private void logStartWithSize(final Path source) {
        Logger.info(
            this,
            "%s '%[file]s' (%[size]s)",
            this.process,
            source,
            LoggedTranslation.size(source)
        );
    }

    private void logEndWithSize(final Path source, final Path after, final long time) {
        Logger.info(
            this,
            "'%[file]s' %s to '%[file]s' (%[size]s) in %[ms]s",
            source,
            this.participle,
            after,
            LoggedTranslation.size(after),
            time
        );
    }

    private static long size(final Path path) {
        try {
            long result;
            if (Files.exists(path) && !path.equals(LoggedTranslation.UNKNOWN)) {
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
