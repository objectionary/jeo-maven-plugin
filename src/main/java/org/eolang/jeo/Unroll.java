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

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.eolang.jeo.representation.CanonicalXmir;

/**
 * Unroll XMIR file.
 * @since 0.6
 */
public final class Unroll implements Transformation {

    /**
     * Source path.
     */
    private final Path src;

    /**
     * Target path.
     */
    private final Path trgt;

    /**
     * XMIR path.
     */
    private final Path xmir;

    /**
     * Constructor.
     * @param source Source folder path.
     * @param target Target folder path.
     * @param xmir XMIR file path.
     */
    public Unroll(final Path source, final Path target, final Path xmir) {
        this.src = source;
        this.trgt = target;
        this.xmir = xmir;
    }

    @Override
    public Path source() {
        return this.xmir;
    }

    @Override
    public Path target() {
        return this.trgt.resolve(this.src.relativize(this.xmir));
    }

    @Override
    public byte[] transform() {
        try {
            return new CanonicalXmir(this.xmir)
                .plain()
                .toString()
                .getBytes(StandardCharsets.UTF_8);
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to unroll XMIR file '%s', most probably the file does not exist",
                    this.xmir
                ),
                exception
            );
        }
    }
}
