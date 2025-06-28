/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Test
    void transpilesSuccessfullyWithoutComments(@TempDir final Path temp) throws IOException {
        final String name = "MethodByte.class";
        Files.write(
            temp.resolve(name),
            new UncheckedBytes(new BytesOf(new ResourceOf(name))).asBytes()
        );
        new Disassembler(
            temp, temp, new DisassembleParams(DisassembleMode.SHORT, false, true, false)
        ).disassemble();
        final Path disassembled = temp.resolve("org")
            .resolve("eolang")
            .resolve("jeo")
            .resolve("MethodByte.xmir");
        MatcherAssert.assertThat(
            String.format("Can't find the transpiled file for the class '%s'.", name),
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
        final String name = "MethodByte.class";
        Files.write(
            temp.resolve(name),
            new UncheckedBytes(new BytesOf(new ResourceOf(name))).asBytes()
        );
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
}
