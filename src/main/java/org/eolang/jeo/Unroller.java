/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.eolang.jeo.representation.CanonicalXmir;

/**
 * Unroller.
 * This class unrolls all the formatting changes made by `phi/unphi` transformations.
 * In other words, it makes XMIR understandable by jeo-maven-plugin after `phi/unphi`
 * transformations.
 * @since 0.6
 */
final class Unroller {

    /**
     * Source directory with XMIR files that were changed by `phi/unphi` transformations.
     */
    private final Path source;

    /**
     * Target directory where unrolled XMIR files will be saved.
     */
    private final Path target;

    /**
     * Constructor.
     * @param source Directory with XMIR files that were changed by `phi/unphi` transformations.
     * @param target Target directory where unrolled XMIR files will be saved.
     */
    Unroller(final Path source, final Path target) {
        this.source = source;
        this.target = target;
    }

    /**
     * Unrolls all the XMIR files from the source directory to the target directory.
     * @return The number of unrolled XMIR files.
     */
    long unroll() {
        try (Stream<Path> xmirs = Files.walk(this.source)) {
            return xmirs.filter(Unroller::isXmir)
                .parallel()
                .peek(this::unroll)
                .count();
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to unroll XMIR files from '%s' directory to '%s'",
                    this.source,
                    this.target
                ),
                exception
            );
        }
    }

    /**
     * Unrolls the XMIR file.
     * @param xmir The path to the XMIR file.
     */
    private void unroll(final Path xmir) {
        try {
            this.prepareThread();
            final UnrollTrans trans = new UnrollTrans(this.source, this.target, xmir);
            final Path output = trans.to();
            Logger.info(this, "Unrolling XMIR file '%s' to '%s'", xmir, output);
            final long start = System.currentTimeMillis();
            final byte[] bytes = trans.transform();
            final long end = System.currentTimeMillis() - start;
            Files.createDirectories(output.getParent());
            Files.write(output, bytes);
            Logger.info(
                this,
                "XMIR file '%s' was unrolled in %[ms]s",
                output.getFileName(),
                end
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to unroll XMIR file '%s', most probably an I/O error occurred",
                    xmir
                ),
                exception
            );
        }
    }

    /**
     * Prepares the thread for unrolling.
     * ATTENTION! DO NOT REMOVE THIS METHOD!
     * This method was added to solve the problem with class loading during the unrolling process.
     * Since we unroll files in parallel, we need to set the context class loader for each thread.
     * Otherwise, the class loader won't be able to find `net.sf.saxon.TransformerFactoryImpl`.
     * This method is a workaround for the problem with class loading during the unrolling process.
     */
    @SuppressWarnings("PMD.UseProperClassLoader")
    private void prepareThread() {
        System.setProperty(
            "javax.xml.transform.TransformerFactory",
            "net.sf.saxon.TransformerFactoryImpl"
        );
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
    }

    /**
     * Checks if the file is an XMIR file.
     * @param path The path to the file.
     * @return True if the file is an XMIR file, false otherwise.
     */
    private static boolean isXmir(final Path path) {
        return Files.isRegularFile(path) && path.toString().endsWith(".xmir");
    }
}
