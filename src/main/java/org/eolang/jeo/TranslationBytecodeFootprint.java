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
        representations.forEach(this::assemble);
        return Collections.unmodifiableCollection(representations);
    }

    /**
     * Recompile the Intermediate Representation.
     *
     * @param representation Intermediate Representation to recompile.
     */
    private void assemble(final Representation representation) {
        final Details details = representation.details();
        final String name = new JavaName(details.name()).decode();
        try {
            final byte[] bytecode = representation.toBytecode().asBytes();
            final String[] subpath = name.split("\\.");
            final Optional<Path> source = details.source();
            if (source.isPresent()) {
                Logger.info(
                    this,
                    "Assembling '%[file]s' (%[size]s)",
                    source.get(),
                    Files.size(source.get())
                );
            }
            final long start = System.currentTimeMillis();
            subpath[subpath.length - 1] = String.format("%s.class", subpath[subpath.length - 1]);
            final Path path = Paths.get(this.classes.toString(), subpath);
            Files.createDirectories(path.getParent());
            Files.write(path, bytecode);
            final long time = System.currentTimeMillis() - start;
            source.ifPresent(
                value -> Logger.info(
                    this,
                    "'%[file]s' assembled to '%[file]s' (%[size]s) in %[ms]s",
                    value,
                    path,
                    (long) bytecode.length,
                    time
                )
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(String.format("Can't recompile '%s'", name), exception);
        }
    }
}
