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
package org.eolang.jeo.improvement;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import org.eolang.jeo.Details;
import org.eolang.jeo.Improvement;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.JavaName;

/**
 * Footprint of the representations as bytecode classes.
 *
 * @since 0.1.0
 */
public final class ImprovementBytecodeFootprint implements Improvement {

    /**
     * Where to save the bytecode classes.
     */
    private final Path classes;

    /**
     * Constructor.
     *
     * @param target Where to save the bytecode classes.
     */
    public ImprovementBytecodeFootprint(final Path target) {
        this.classes = target;
    }

    @Override
    public Collection<? extends Representation> apply(
        final Collection<? extends Representation> representations
    ) {
        Logger.info(this, "Writing .class files to %s", this.classes);
        representations.forEach(this::recompile);
        return Collections.unmodifiableCollection(representations);
    }

    /**
     * Recompile the Intermediate Representation.
     *
     * @param representation Intermediate Representation to recompile.
     */
    private void recompile(final Representation representation) {
        final Details details = representation.details();
        final String name = new JavaName(details.name()).decode();
        try {
            final byte[] bytecode = representation.toBytecode().asBytes();
            final String[] subpath = name.split("\\.");
            subpath[subpath.length - 1] = String.format("%s.class", subpath[subpath.length - 1]);
            final Path path = Paths.get(this.classes.toString(), subpath);
            Files.createDirectories(path.getParent());
            Files.write(path, bytecode);
            Logger.info(
                this,
                String.format(
                    "%s compiled into %s (%d bytes)",
                    details.source(),
                    path.getFileName().toString(),
                    bytecode.length
                )
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(String.format("Can't recompile '%s'", name), exception);
        }
    }
}
