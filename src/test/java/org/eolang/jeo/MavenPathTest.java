/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test case for {@link MavenPath}.
 *
 * @since 0.15.0
 */
final class MavenPathTest {

    @ParameterizedTest
    @CsvSource({
        "src/test/resources, src/test/resources",
        "${project.basedir}/src/test/resources, ./src/test/resources",
        "${basedir}/src/test/resources, ./src/test/resources",
        "${project.build.outputDirectory}/extra, target/classes/extra",
        "${project.build.directory}/extra, target/extra"
    })
    void resolvesPath(final String input, final String expected) {
        MatcherAssert.assertThat(
            "Path resolved correctly",
            new MavenPath(new File(input)).resolve(),
            Matchers.equalTo(Paths.get(expected))
        );
    }

    @Test
    void throwsExceptionWhenResolvingNullFile() {
        MatcherAssert.assertThat(
            "Null file should throw IllegalArgumentException",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new MavenPath((File) null).resolve(),
                "Resolving null File must be rejected"
            ).getMessage(),
            Matchers.containsString("Path can't be null")
        );
    }

    @Test
    void throwsExceptionWhenResolvingNullString() {
        MatcherAssert.assertThat(
            "Null string should throw IllegalArgumentException",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new MavenPath((String) null).resolve(),
                "Resolving null String must be rejected"
            ).getMessage(),
            Matchers.containsString("Path can't be null")
        );
    }

    @Test
    void throwsExceptionWhenResolvingNullNioPath() {
        MatcherAssert.assertThat(
            "Null Path should throw IllegalArgumentException",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new MavenPath((Path) null).resolve(),
                "Resolving null Path must be rejected"
            ).getMessage(),
            Matchers.containsString("Path can't be null")
        );
    }

    @Test
    void resolvesPathFromString() {
        MatcherAssert.assertThat(
            "Path from string must be resolved correctly",
            new MavenPath("${project.basedir}/src").resolve(),
            Matchers.equalTo(Paths.get("./src"))
        );
    }

    @Test
    void resolvesPathFromNioPath() {
        MatcherAssert.assertThat(
            "Path from NIO Path must be resolved correctly",
            new MavenPath(
                Paths.get("${project.build.directory}/classes")
            ).resolve(),
            Matchers.equalTo(Paths.get("target/classes"))
        );
    }

    @Test
    void resolvesPathFromZipFilesystem(@TempDir final Path temp) throws IOException {
        try (
            FileSystem zipfs = FileSystems.newFileSystem(
                URI.create(String.format("jar:%s", temp.resolve("sample.zip").toUri())),
                Collections.singletonMap("create", "true")
            )
        ) {
            MatcherAssert.assertThat(
                "Path from ZIP filesystem must be resolved without UnsupportedOperationException",
                new MavenPath(zipfs.getPath("/classes")).resolve(),
                Matchers.equalTo(Paths.get("/classes"))
            );
        }
    }
}
