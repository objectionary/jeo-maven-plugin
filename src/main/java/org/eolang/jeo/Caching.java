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
 * Cached transformation.
 * @since 0.6
 */
public final class Caching implements Transformation {

    /**
     * Original transformation.
     */
    private final Transformation origin;

    /**
     * Constructor.
     * @param origin Original transformation.
     */
    Caching(final Transformation origin) {
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
        try {
            return this.tryTransform();
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to transform of '%s' to '%s'",
                    this.source(),
                    this.target()
                ),
                exception
            );
        }
    }

    /**
     * Try to transform the file.
     * @return The transformed file content.
     * @throws IOException If something goes wrong.
     */
    private byte[] tryTransform() throws IOException {
        final byte[] result;
        final Path target = this.target();
        if (this.alreadyTransformed()) {
            Logger.info(
                this,
                "The file '%s' is already transformed to '%s'. Skipping.",
                this.source(),
                target
            );
            result = Files.readAllBytes(target);
        } else {
            final byte[] transform = this.origin.transform();
            Files.createDirectories(target.getParent());
            Files.write(target, transform);
            result = transform;
        }
        return result;
    }

    /**
     * Check if the file has already been transformed.
     * @return True if the file has already been transformed.
     * @throws IOException If something goes wrong.
     */
    private boolean alreadyTransformed() throws IOException {
        final Path source = this.source();
        final Path target = this.target();
        return Files.exists(target)
            && Files.exists(source)
            && Files.getLastModifiedTime(target).compareTo(Files.getLastModifiedTime(source)) >= 0;
    }
}
