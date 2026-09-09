/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link BytecodeClasses}.
 * @since 0.14.0
 */
final class BytecodeClassesTest {

    @Test
    void convertsToString() {
        final Path path = Paths.get("src/test/resources/bytecode-classes");
        MatcherAssert.assertThat(
            "BytecodeClasses toString() should return the correct path",
            new BytecodeClasses(path).toString(),
            Matchers.equalTo(path.toString())
        );
    }

    @Test
    void throwsDescriptiveExceptionWhenConvertingNullInputToString() {
        final IllegalStateException exception = Assertions.assertThrows(
            IllegalStateException.class,
            () -> new BytecodeClasses(null).toString()
        );
        MatcherAssert.assertThat(
            "BytecodeClasses toString() should describe the missing classes directory",
            exception.getMessage(),
            Matchers.containsString("The classes directory is not set")
        );
    }

    @Test
    void throwsDescriptiveExceptionWhenGettingRootFromNullInput() {
        final IllegalStateException exception = Assertions.assertThrows(
            IllegalStateException.class,
            () -> new BytecodeClasses(null).root()
        );
        MatcherAssert.assertThat(
            "BytecodeClasses root() should describe the missing classes directory",
            exception.getMessage(),
            Matchers.containsString("The classes directory is not set")
        );
    }

    @Test
    void doesNotThrowExceptionOnAbsentDirectory() {
        final Path path = Paths.get("/dev/null/absent");
        MatcherAssert.assertThat(
            "BytecodeClasses should handle empty directories without throwing exceptions",
            new BytecodeClasses(path).all().count(),
            Matchers.equalTo(0L)
        );
    }

    @Test
    void findsAllClassFilesInDirectory(@TempDir final Path temp) throws IOException {
        Files.createDirectories(temp.resolve("com"));
        Files.write(
            temp.resolve("com").resolve("Foo.class"),
            new BytecodeObject(new BytecodeClass("Foo")).bytecode().bytes()
        );
        Files.write(
            temp.resolve("Bar.class"),
            new BytecodeObject(new BytecodeClass("Bar")).bytecode().bytes()
        );
        Files.write(
            temp.resolve("note.txt"),
            "not a class file".getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "all() must return only the .class files from the whole directory tree",
            new BytecodeClasses(temp).all().count(),
            Matchers.equalTo(2L)
        );
    }
}

