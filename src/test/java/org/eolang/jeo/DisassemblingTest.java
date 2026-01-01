/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link Disassembling}.
 * @since 0.15.0
 */
final class DisassemblingTest {

    @Test
    void generatesCorrectOutputTargetForOpenModuleInfo(@TempDir final Path tmp) throws Exception {
        final ResourceOf obytecode = new ResourceOf("open-module-info.class");
        final Path open = tmp.resolve("META-INF").resolve("module-info.class");
        Files.createDirectories(open.getParent());
        Files.write(open, new BytesOf(obytecode).asBytes());
        final String output = "target";
        final Path target = tmp.resolve(output);
        MatcherAssert.assertThat(
            "Disassembling should create correct target path for open module-info",
            new Disassembling(tmp, target, open, new Format()).target(),
            Matchers.equalTo(
                tmp.resolve(output).resolve("META-INF").resolve("module-info.xmir")
            )
        );
    }

    @Test
    void generatesCorrectOutputTargetForClosedModuleInfo(@TempDir final Path tmp) throws Exception {
        final ResourceOf cbytecode = new ResourceOf("closed-module-info.class");
        final Path closed = tmp.resolve("module-info.class");
        Files.write(closed, new BytesOf(cbytecode).asBytes());
        final String output = "t";
        final Path target = tmp.resolve(output);
        MatcherAssert.assertThat(
            "Disassembling should create correct target path for closed module-info",
            new Disassembling(tmp, target, closed, new Format()).target(),
            Matchers.equalTo(
                tmp.resolve(output).resolve("module-info.xmir")
            )
        );
    }

    @Test
    void generatesCorrectTargetForPlainClass(@TempDir final Path tmp) throws Exception {
        final String name = "Check.class";
        final Path plain = tmp.resolve("org")
            .resolve("eolang")
            .resolve("jeo")
            .resolve(name);
        Files.createDirectories(plain.getParent());
        Files.write(plain, new BytesOf(new ResourceOf(name)).asBytes());
        final Path target = tmp.resolve("tgt");
        MatcherAssert.assertThat(
            "Disassembling should create correct target path for plain class",
            new Disassembling(tmp, target, plain, new Format()).target(),
            Matchers.equalTo(
                target.resolve("org")
                    .resolve("eolang")
                    .resolve("jeo")
                    .resolve("Check.xmir")
            )
        );
    }
}
