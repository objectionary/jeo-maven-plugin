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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.PrefixedName;
import org.eolang.jeo.representation.xmir.AllLabels;

/**
 * Assemble a representation.
 * @since 0.2
 */
public final class Assemble implements Translation {

    /**
     * Classes path.
     */
    private final Path classes;

    /**
     * Constructor.
     * @param classes Classes path.
     */
    public Assemble(final Path classes) {
        this.classes = classes;
    }

    @Override
    public Representation apply(final Representation representation) {
        new AllLabels().clearCache();
        final Details details = representation.details();
        final String name = new PrefixedName(details.name()).decode();
        try {
            final byte[] bytecode = representation.toBytecode().asBytes();
            final String[] subpath = name.split("\\.");
            subpath[subpath.length - 1] = String.format("%s.class", subpath[subpath.length - 1]);
            final Path path = Paths.get(this.classes.toString(), subpath);
            Files.createDirectories(path.getParent());
            Files.write(path, bytecode);
            return new BytecodeRepresentation(path);
        } catch (final IOException exception) {
            throw new IllegalStateException(String.format("Can't recompile '%s'", name), exception);
        }
    }
}
