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
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link EoRepresentations}.
 *
 * @since 0.1.0
 */
final class EoRepresentationsTest {

    @Test
    void retrievesObjectsSuccessfully(@TempDir final Path temp) throws IOException {
        final int expected = 2;
        final Path directory = temp.resolve(new XmirDefaultDirectory().toPath());
        Files.createDirectories(directory);
        Files.write(
            directory.resolve("first.xmir"),
            new BytecodeClass("First").xml().toString().getBytes(StandardCharsets.UTF_8)
        );
        Files.write(
            directory.resolve("second.xmir"),
            new BytecodeClass("Second").xml().toString().getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            String.format("Objects were not retrieved, we expected '%d' objects", expected),
            new EoRepresentations(temp).objects(),
            Matchers.hasSize(expected)
        );
    }

    @Test
    void retrievesEmptyObjectsIfFolderIsEmpty(@TempDir final Path temp) throws IOException {
        Files.createDirectories(temp.resolve(new XmirDefaultDirectory().toPath()));
        MatcherAssert.assertThat(
            "Objects were not retrieved, we expected empty list",
            new EoRepresentations(temp).objects(),
            Matchers.empty()
        );
    }

    @Test
    void throwsExceptionIfFolderDoesNotExist(@TempDir final Path temp) {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new EoRepresentations(temp).objects(),
            "Exception was not thrown when folder does not exist"
        );
    }

    @Test
    void usesDifferentSubfolder(@TempDir final Path temp) throws IOException {
        final Path generated = temp.resolve("generated-sources");
        final Path directory = Paths.get("opeo-xmir");
        Files.createDirectories(generated.resolve(directory));
        Files.write(
            generated.resolve(directory).resolve("opeo-class.xmir"),
            new BytecodeClass("OpeoClass").xml().toString().getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            String.format(
                "Objects were not retrieved, we expected exactly one object was read from %s",
                directory
            ),
            new EoRepresentations(generated, directory).objects(),
            Matchers.hasSize(1)
        );
    }
}
