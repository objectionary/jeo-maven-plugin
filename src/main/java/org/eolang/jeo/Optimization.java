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
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Optimization by using the EO language.
 * The entrypoint class for the optimization.
 *
 * @since 0.1.0
 * @todo #13:90min Implement back transformation to bytecode.
 *  The Optimization class should transform the IRs back to bytecode.
 *  When the back transformation is ready, remove that puzzle.
 *  Don't forget to add unit tests for the back transformation.
 */
final class Optimization {

    /**
     * Project compiled classes.
     */
    private final Path classes;

    /**
     * Improvements to apply.
     */
    private final Boost boosts;

    /**
     * Ctor.
     * @param classes Project compiled classes.
     * @param boost Improvements to apply.
     */
    Optimization(final Path classes, final Boost boost) {
        this.classes = classes;
        this.boosts = boost;
    }

    /**
     * Apply the optimization.
     * @throws IOException If some I/O problem arises.
     */
    void apply() throws IOException {
        this.boosts.apply(
            this.bytecode()
                .stream()
                .map(BytecodeIr::new)
                .collect(Collectors.toList())
        ).forEach(this::recompile);
    }

    private void recompile(final IR ir) {
        final String name = ir.name();
        try {
            final byte[] bytecode = ir.toBytecode();
            final String[] subpath = name.split("\\.");
            subpath[subpath.length - 1] = String.format("%s.class", subpath[subpath.length - 1]);
            final Path path = Paths.get(this.classes.toString(), subpath);
            Logger.info(
                this,
                "Recompiling '%s', bytecode instance '%s', bytes to save '%s'",
                path,
                ir.getClass(),
                bytecode.length
            );
            Files.createDirectories(path.getParent());
            Files.write(path, bytecode);
        } catch (final IOException exception) {
            throw new IllegalStateException(String.format("Can't recompile '%s'", name), exception);
        }
    }

    /**
     * Find all bytecode files.
     * @return Collection of bytecode files
     * @throws IOException If some I/O problem arises
     */
    private Collection<Path> bytecode() throws IOException {
        if (Objects.isNull(this.classes)) {
            throw new IllegalStateException(
                "The classes directory is not set, jeo-maven-plugin does not know where to look for classes."
            );
        }
        try (Stream<Path> walk = Files.walk(this.classes)) {
            return walk.filter(path -> path.toString().endsWith(".class"))
                .collect(Collectors.toList());
        }
    }
}
