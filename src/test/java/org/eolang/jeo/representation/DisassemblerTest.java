/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.Disassembler;
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
    void transpilesSuccessfully(@TempDir final Path temp) throws IOException {
        final String name = "MethodByte.class";
        Files.write(
            temp.resolve(name),
            new UncheckedBytes(new BytesOf(new ResourceOf(name))).asBytes()
        );
        new Disassembler(temp, temp).disassemble();
        MatcherAssert.assertThat(
            String.format("Can't find the transpiled file for the class '%s'.", name),
            Files.exists(
                temp.resolve("org")
                    .resolve("eolang")
                    .resolve("jeo")
                    .resolve("MethodByte.xmir")
            ),
            Matchers.is(true)
        );
    }
}
