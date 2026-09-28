/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test cases for {@link Disassembler}.
 *
 * @since 0.15
 */
final class DisassemblerTest {

    @Test
    void walksClassesOnlyOnce(@TempDir final Path temp) throws Exception {
        final Path classes = temp.resolve("classes");
        Files.createDirectories(classes);
        Files.write(
            classes.resolve("MethodByte.class"),
            new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
        );
        final List<String> logs = new CopyOnWriteArrayList<>();
        new Disassembler(
            new FilteredClasses(
                new BytecodeClasses(classes),
                new GlobFilter(Collections.singleton("**"), Collections.emptySet()),
                logs::add
            ),
            temp.resolve("xmir"),
            new Format(),
            false
        ).disassemble();
        MatcherAssert.assertThat(
            "The list of classes must be found once per run, not once for counting and once for work",
            logs,
            Matchers.hasSize(1)
        );
    }
}
