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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.BytecodeRepresentation;

/**
 * Project compiled classes.
 *
 * @since 0.1.0
 */
public final class BytecodeClasses {

    /**
     * Project compiled classes.
     */
    private final Path classpath;

    /**
     * Constructor.
     * @param classes Folder with compiled classes.
     */
    public BytecodeClasses(final Path classes) {
        this.classpath = classes;
    }

    /**
     * Get all bytecode files as a collection of {@link org.eolang.jeo.Representation}.
     * @return Collection of {@link org.eolang.jeo.Representation}
     * @throws IOException If some I/O problem arises.
     */
    public Collection<BytecodeRepresentation> bytecode() throws IOException {
        return this.classes()
            .stream()
            .map(BytecodeRepresentation::new)
            .collect(Collectors.toList());
    }

    /**
     * Find all bytecode files.
     * @return Collection of bytecode files
     * @throws java.io.IOException If some I/O problem arises
     */
    private Collection<Path> classes() throws IOException {
        if (Objects.isNull(this.classpath)) {
            throw new IllegalStateException(
                "The classes directory is not set, jeo-maven-plugin does not know where to look for classes."
            );
        }
        if (!Files.exists(this.classpath)) {
            throw new IllegalStateException(
                String.format(
                    "The classes directory '%s' does not exist, jeo-maven-plugin does not know where to look for classes.",
                    this.classpath
                )
            );
        }
        try (Stream<Path> walk = Files.walk(this.classpath)) {
            return walk.filter(path -> path.toString().endsWith(".class"))
                .collect(Collectors.toList());
        }
    }
}
