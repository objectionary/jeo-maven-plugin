/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link Assembling}.
 * @since 0.15.0
 */
final class AssemblingTest {

    /**
     * Safe mock path.
     */
    private static final Path DEVNULL = Paths.get("/dev/null");

    @Test
    void bypassesRepresentationAsSource() {
        final Path expected = Paths.get("source");
        MatcherAssert.assertThat(
            "Assembling must return the source path it was constructed with",
            new Assembling(
                AssemblingTest.DEVNULL,
                AssemblingTest.DEVNULL,
                expected
            ).source(),
            Matchers.equalTo(expected)
        );
    }

    /**
     * Checks that Assembling.target() returns the correct target path.
     * @param src Source directory
     * @param tgt Target directory
     * @param file Source file
     * @param expected Expected target file
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    @ParameterizedTest
    @MethodSource("targetDirs")
    void determinesCorrectTargetFile(
        final Path src,
        final Path tgt,
        final Path file,
        final Path expected
    ) {
        MatcherAssert.assertThat(
            "Assembling must return the target path it was constructed with",
            new Assembling(
                src,
                tgt,
                file
            ).target(),
            Matchers.equalTo(expected)
        );
    }

    /**
     * Test cases for {@link #determinesCorrectTargetFile}.
     * @return Test cases
     */
    private static Stream<Arguments> targetDirs() {
        return Stream.of(
            Arguments.of(
                Paths.get("src"),
                Paths.get("out"),
                Paths.get("src/org/eolang/jeo/representation/xmir/XmlValue.xmir"),
                Paths.get("out/org/eolang/jeo/representation/xmir/XmlValue.class")
            ),
            Arguments.of(
                Paths.get("srcsrc"),
                Paths.get("outout"),
                Paths.get("srcsrc/XmlValue.xmir"),
                Paths.get("outout/XmlValue.class")
            ),
            Arguments.of(
                Paths.get("a/b/c"),
                Paths.get("d/e/f"),
                Paths.get("a/b/c/x/y/z/File.xmir"),
                Paths.get("d/e/f/x/y/z/File.class")
            ),
            Arguments.of(
                Paths.get(""),
                Paths.get("nonempty"),
                Paths.get("File.xmir"),
                Paths.get("nonempty/File.class")
            )
        );
    }
}
