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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Assembler.
 * This class is used to assemble the project's EO source files to bytecode.
 * @since 0.2
 */
final class Assembler {

    /**
     * Input folder with "xmir" files.
     */
    private final Path input;

    /**
     * Output folder for the assembled classes.
     */
    private final Path output;

    /**
     * Constructor.
     * @param input Input folder with "xmir" files.
     * @param output Output folder for the assembled classes.
     */
    Assembler(final Path input, final Path output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Assemble all "xmir" files.
     * @since 0.2
     */
    void assemble() {
        final String assembling = "Assembling";
        final String assembled = "assembled";
        final Stream<Path> stream = new LoggedTranslator(
            assembling,
            assembled,
            this.input,
            this.output,
            new BatchedTranslator(
                new LoggedTranslation(
                    assembling,
                    assembled,
                    new Assemble(this.output)
                )
            )
        ).apply(new XmirRepresentations(this.input).paths());
        stream.forEach(
            terminated -> {
                try {
                    Logger.info(
                        this,
                        "Assembling of '%s' (%[size]s) finished successfully.",
                        terminated,
                        Files.size(terminated)
                    );
                } catch (final IOException exception) {
                    throw new IllegalStateException(
                        String.format("Can't get size of '%s'", terminated),
                        exception
                    );
                }
            }
        );
        stream.close();
    }
}
