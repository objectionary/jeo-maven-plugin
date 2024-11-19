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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link ParallelTranslator}.
 *
 * @since 0.1.0
 */
final class ParallelTranslatorTest {

    /**
     * Where the XML file is expected to be saved.
     */
    private final Path expected = Paths.get("Application.xmir");

    @Test
    void savesXml(@TempDir final Path temp) throws IOException {
        final Path clazz = temp.resolve("Application.class");
        Files.write(
            clazz,
            new BytecodeProgram(
                "org/eolang/jeo",
                new BytecodeClass("Application")
            ).bytecode().bytes()
        );
        new ParallelTranslator(ParallelTranslatorTest::transform)
            .apply(Stream.of(clazz))
            .collect(Collectors.toList());
        MatcherAssert.assertThat(
            "XML file was not saved",
            temp.resolve(this.expected).toFile(),
            FileMatchers.anExistingFile()
        );
    }

    @Test
    void overwritesXml(@TempDir final Path temp) throws IOException {
        final Path path = Paths.get(temp.toString(), "Application.class");
        Files.createDirectories(path.getParent());
        Files.write(
            path,
            new BytecodeProgram(
                "org/eolang/jeo",
                new BytecodeClass("Application")
            ).bytecode().bytes()
        );
        final ParallelTranslator trans = new ParallelTranslator(ParallelTranslatorTest::transform);
        trans.apply(Stream.of(path)).collect(Collectors.toList());
        trans.apply(Stream.of(path)).collect(Collectors.toList());
        MatcherAssert.assertThat(
            "XML file was not successfully overwritten",
            temp.resolve(this.expected).toFile(),
            FileMatchers.anExistingFile()
        );
    }

    @Test
    void assemblesSuccessfully(@TempDir final Path temp) throws IOException {
        final String fake = "Fake";
        final Path path = temp.resolve("jeo")
            .resolve("xmir")
            .resolve(String.format("%s.xmir", fake));
        Files.createDirectories(path.getParent());
        Files.write(
            path,
            new BytecodeProgram(
                "jeo/xmir",
                new BytecodeClass(fake)
            ).xml().toString().getBytes(StandardCharsets.UTF_8)
        );
        new ParallelTranslator(ParallelTranslatorTest::transform)
            .apply(Stream.of(path))
            .collect(Collectors.toList());
        MatcherAssert.assertThat(
            String.format(
                "Bytecode file was not saved for the representation with the name '%s'",
                fake
            ),
            temp.resolve("jeo")
                .resolve("xmir")
                .resolve("Fake.class")
                .toFile(),
            FileMatchers.anExistingFile()
        );
    }

    /**
     * Transform the path.
     * @param path Path to transform.
     * @return Transformed path.
     */
    private static Path transform(final Path path) {
        final Path result;
        try {
            final String name = path.getFileName().toString();
            if (name.endsWith(".class")) {
                final Path target = path.resolveSibling(name.replace(".class", ".xmir"));
                Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
                result = target;
            } else if (name.endsWith(".xmir")) {
                final Path target = path.resolveSibling(name.replace(".xmir", ".class"));
                Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
                result = target;
            } else {
                throw new IllegalArgumentException(String.format("Unexpected file type: %s", name));
            }
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't transform file '%s'", path),
                exception
            );
        }
        return result;
    }
}
