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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Optimization}.
 *
 * @since 0.1.0
 * @todo #24:90min Add unit tests for Optimization class.
 *  We still have to add the next unit tests:
 *  - The classes directory is empty.
 *  - The classes directory does not exist.
 *  - The classes directory has some clases.
 *  - The classes directory has some classes and some subdirectories.
 *  - The classes directory has some classes and some garbage.
 *  When the unit tests are ready, remove that puzzle.
 */
class OptimizationTest {

    @Test
    void appliesAllBoosts(@TempDir final Path classes) throws IOException {
        final Path directory = Paths.get("org")
            .resolve("eolang")
            .resolve("jeo");
        final String name = "MethodByte.class";
        final Path clazz = classes.resolve(directory).resolve(name);
        Files.createDirectories(clazz.getParent());
        Files.write(
            clazz,
            new UncheckedBytes(new BytesOf(new ResourceOf(name))).asBytes(),
            StandardOpenOption.CREATE_NEW
        );
        final Boost.Mock boost = new Boost.Mock();
        new Optimization(classes, boost).apply();
        MatcherAssert.assertThat(
            "Boost was not applied",
            boost.isApplied(),
            Matchers.equalTo(true)
        );
        MatcherAssert.assertThat(
            "Optimization should save final bytecode into appropriate directory",
            clazz.toFile(),
            FileMatchers.anExistingFile()
        );
    }
}
