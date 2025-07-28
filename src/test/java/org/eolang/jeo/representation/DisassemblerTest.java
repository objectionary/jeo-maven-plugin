/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.Disassembler;
import org.eolang.jeo.representation.asm.DisassembleMode;
import org.eolang.jeo.representation.asm.DisassembleParams;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link Disassembler}.
 *
 * @since 0.1.0
 */
final class DisassemblerTest {

    /**
     * Bytecode class name.
     */
    private static final String CLASS_NAME = "MethodByte.class";

    /**
     * Bytecode of a java class.
     */
    private static final byte[] BYTECODE = new UncheckedBytes(
        new BytesOf(new ResourceOf(DisassemblerTest.CLASS_NAME))
    ).asBytes();

    @Test
    void transpilesSuccessfullyWithoutComments(@TempDir final Path temp) throws IOException {
        Files.write(temp.resolve(DisassemblerTest.CLASS_NAME), DisassemblerTest.BYTECODE);
        new Disassembler(
            temp, temp, new DisassembleParams(DisassembleMode.SHORT, false, true, false)
        ).disassemble();
        final Path disassembled = temp.resolve("org")
            .resolve("eolang")
            .resolve("jeo")
            .resolve("MethodByte.xmir");
        MatcherAssert.assertThat(
            String.format(
                "Can't find the transpiled file for the class '%s'.",
                DisassemblerTest.CLASS_NAME
            ),
            Files.exists(disassembled),
            Matchers.is(true)
        );
        final List<String> lines = Files.readAllLines(disassembled);
        MatcherAssert.assertThat(
            String.format(
                "The XMIR file is not indented correctly, expected 4 spaces  indentation for each 'meta' element, but was '%s'",
                lines
            ),
            lines.stream()
                .filter(line -> line.contains("<meta>"))
                .allMatch(line -> line.startsWith("    ")),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "The XMIR file should not contain comments",
            lines.stream().noneMatch(line -> line.contains("<!--")),
            Matchers.is(true)
        );
    }

    @Test
    void transpilesWithComments(@TempDir final Path temp) throws IOException {
        Files.write(temp.resolve(DisassemblerTest.CLASS_NAME), DisassemblerTest.BYTECODE);
        new Disassembler(
            temp, temp, new DisassembleParams(DisassembleMode.SHORT, true, true, true)
        ).disassemble();
        final List<String> lines = Files.readAllLines(
            temp.resolve("org")
                .resolve("eolang")
                .resolve("jeo")
                .resolve("MethodByte.xmir")
        );
        MatcherAssert.assertThat(
            "The XMIR file should contain comments",
            lines.stream().anyMatch(line -> line.contains("<!--")),
            Matchers.is(true)
        );
    }

    /**
     * This test was added to check the 1176 issue.
     * <p>
     * You can read more about it right here:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1176">link</a>
     * </p>
     * <p>
     * Pay attention!
     * {@link Files#setLastModifiedTime(Path, FileTime)} is used here in order to avoid
     * retrieving disassemble results from the cache.
     * </p>
     * @param temp Temp directory where we make disassembling.
     * @throws IOException In case of any writing or reading error.
     */
    @Test
    void overwritesExistingXmir(@TempDir final Path temp) throws IOException {
        Files.write(temp.resolve(DisassemblerTest.CLASS_NAME), DisassemblerTest.BYTECODE);
        new Disassembler(temp, temp).disassemble();
        final Path disassembled = temp.resolve("org")
            .resolve("eolang")
            .resolve("jeo")
            .resolve("MethodByte.xmir");
        final String appended = "<bottomline>I'm still here</bottomline>";
        Files.write(
            disassembled,
            appended.getBytes(StandardCharsets.UTF_8),
            StandardOpenOption.APPEND
        );
        MatcherAssert.assertThat(
            "We expect the XMIR file contain appended text",
            new String(Files.readAllBytes(disassembled), StandardCharsets.UTF_8),
            Matchers.containsString(appended)
        );
        Files.setLastModifiedTime(disassembled, FileTime.from(Instant.EPOCH));
        new Disassembler(temp, temp).disassemble();
        MatcherAssert.assertThat(
            "We expect the appended text to be disappeared after the second disassembling",
            new String(Files.readAllBytes(disassembled), StandardCharsets.UTF_8),
            Matchers.not(Matchers.containsString(appended))
        );
    }
}
