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
package org.eolang.jeo.representation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.improvement.XmirFootprint;

/**
 * Transpilation of the bytecode to the EO.
 *
 * @since 0.1.0
 * @todo #58:30min Duplicate between BytecodeTranspilation and Optimization.
 *  Both classes do the same thing, but in different ways. We need to
 *  refactor them to use the same approach. When it's done, remove this
 *  puzzle. Classes:
 *  - {@link org.eolang.jeo.representation.BytecodeTranspilation}
 *  - {@link org.eolang.jeo.Optimization}
 */
public class BytecodeTranspilation {

    /**
     * Project compiled classes.
     */
    private final Path classes;

    /**
     * Project default target directory.
     */
    private final Path target;

    /**
     * Constructor.
     * @param classes Project compiled classes.
     * @param target Project default target directory.
     */
    public BytecodeTranspilation(
        final Path classes,
        final Path target
    ) {
        this.classes = classes;
        this.target = target;
    }

    /**
     * Transpile all bytecode files.
     * @throws IOException If some I/O problem arises.
     */
    public void transpile() throws IOException {
        new XmirFootprint(this.target).apply(
            this.bytecode()
                .stream()
                .map(BytecodeRepresentation::new)
                .collect(Collectors.toList())
        );
    }

    /**
     * Find all bytecode files.
     * @return Collection of bytecode files
     * @throws java.io.IOException If some I/O problem arises
     */
    private Collection<Path> bytecode() throws IOException {
        if (Objects.isNull(this.classes)) {
            throw new IllegalStateException(
                "The classes directory is not set, jeo-maven-plugin does not know where to look for classes."
            );
        }
        if (!Files.exists(this.classes)) {
            throw new IllegalStateException(
                String.format(
                    "The classes directory '%s' does not exist, jeo-maven-plugin does not know where to look for classes.",
                    this.classes
                )
            );
        }
        try (Stream<Path> walk = Files.walk(this.classes)) {
            return walk.filter(path -> path.toString().endsWith(".class"))
                .collect(Collectors.toList());
        }
    }
}
