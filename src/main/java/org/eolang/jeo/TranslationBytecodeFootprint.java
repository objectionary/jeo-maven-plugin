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
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import org.eolang.jeo.representation.JavaName;

/**
 * Footprint of the representations as bytecode classes.
 *
 * @since 0.1.0
 */
public final class TranslationBytecodeFootprint implements Translation {

    /**
     * The folder from where to read the .xmir files.
     * This field is used for logging purposes only.
     * @since 0.2
     */
    private final Path from;

    /**
     * Where to save the bytecode classes.
     */
    private final Path classes;

    /**
     * Constructor.
     * @param from The folder from where to read the .xmir files.
     * @param classes Where to save the bytecode classes.
     */
    public TranslationBytecodeFootprint(final Path from, final Path classes) {
        this.from = from;
        this.classes = classes;
    }

    @Override
    public Collection<? extends Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        Logger.info(
            this, "Assembling .xmir files from %[file]s to %[file]s",
            this.from,
            this.classes
        );
        representations.forEach(this::assemble);
        Logger.info(this, "Assembled total %d .class files", representations.size());
        return Collections.unmodifiableCollection(representations);
    }

    /**
     * Recompile the Intermediate Representation.
     *
     * @param representation Intermediate Representation to recompile.
     * @todo #431:30min Measure the assembling time and print it to logs.
     *  The assembling time should be measured in milliseconds and printed to logs.
     *  Moreover, we have to add one more log entry that would print the path of the file
     *  being assembled. The entire log should look like this:
     *  "Assembling file.xmir (5kb)....".
     *  "Assembled file.class (6kb) in 100ms".
     */
    private void assemble(final Representation representation) {
        final Details details = representation.details();
        final String name = new JavaName(details.name()).decode();
        try {
            final byte[] bytecode = representation.toBytecode().asBytes();
            final String[] subpath = name.split("\\.");
            subpath[subpath.length - 1] = String.format("%s.class", subpath[subpath.length - 1]);
            final Path path = Paths.get(this.classes.toString(), subpath);
            Files.createDirectories(path.getParent());
            Files.write(path, bytecode);
            details.source().ifPresent(
                value -> Logger.info(
                    this,
                    "%s assembled to %[file]s (%[size]s)",
                    value,
                    path,
                    (long) bytecode.length
                )
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(String.format("Can't recompile '%s'", name), exception);
        }
    }
}
