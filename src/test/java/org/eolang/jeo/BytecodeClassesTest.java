/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link BytecodeClasses}.
 *
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
        MatcherAssert.assertThat(
            "BytecodeClasses toString() should describe the missing classes directory",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new BytecodeClasses(null).toString()
            ).getMessage(),
            Matchers.containsString("The classes directory is not set")
        );
    }

    @Test
    void throwsDescriptiveExceptionWhenGettingRootFromNullInput() {
        MatcherAssert.assertThat(
            "BytecodeClasses root() should describe the missing classes directory",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new BytecodeClasses(null).root()
            ).getMessage(),
            Matchers.containsString("The classes directory is not set")
        );
    }

    @Test
    void doesNotThrowExceptionOnAbsentDirectory() {
        MatcherAssert.assertThat(
            "BytecodeClasses should handle empty directories without throwing exceptions",
            new BytecodeClasses(Paths.get("/dev/null/absent")).all().count(),
            Matchers.equalTo(0L)
        );
    }

    @Test
    void findsClassesInNestedSubdirectories(@TempDir final Path temp) throws Exception {
        final Path nested = temp.resolve("a").resolve("b");
        Files.createDirectories(nested);
        Files.write(
            nested.resolve("MethodByte.class"),
            new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
        );
        MatcherAssert.assertThat(
            "BytecodeClasses should find class files in nested directories",
            new BytecodeClasses(temp).all().count(),
            Matchers.equalTo(1L)
        );
    }

    @Test
    void followsSymlinkedClassesDirectory(@TempDir final Path temp) throws Exception {
        final Path real = temp.resolve("real");
        Files.createDirectories(real);
        Files.write(
            real.resolve("MethodByte.class"),
            new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
        );
        final Path linked = temp.resolve("linked");
        BytecodeClassesTest.symlink(linked, real);
        MatcherAssert.assertThat(
            "BytecodeClasses should traverse a symlinked root classes directory",
            new BytecodeClasses(linked).all().count(),
            Matchers.equalTo(1L)
        );
    }

    @Test
    void followsSymlinkedSubdirectory(@TempDir final Path temp) throws Exception {
        final Path target = temp.resolve("target");
        Files.createDirectories(target);
        Files.write(
            target.resolve("MethodByte.class"),
            new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
        );
        final Path root = temp.resolve("root");
        Files.createDirectories(root);
        BytecodeClassesTest.symlink(root.resolve("sub"), target);
        MatcherAssert.assertThat(
            "BytecodeClasses should traverse symlinked subdirectories inside classes root",
            new BytecodeClasses(root).all().count(),
            Matchers.equalTo(1L)
        );
    }

    private static void symlink(final Path link, final Path target) throws IOException {
        try {
            Files.createSymbolicLink(link, target);
        } catch (final FileSystemException exception) {
            Assumptions.assumeTrue(false, exception.getMessage());
        }
    }
}
