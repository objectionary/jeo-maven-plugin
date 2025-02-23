/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.maven.project.MavenProject;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link PluginStartup}.
 *
 * @since 0.6
 */
final class PluginStartupTest {

    @Test
    void loadsClassesDynamically(@TempDir final Path dir) throws Exception {
        final String name = "SomeClassCompiledDynamically";
        Files.write(
            dir.resolve("SomeClassCompiledDynamically.class"),
            new BytecodeProgram(new BytecodeClass(name)).bytecode().bytes()
        );
        new PluginStartup(new MavenProject(), dir).init();
        MatcherAssert.assertThat(
            "We expect the class to be loaded",
            Thread.currentThread().getContextClassLoader().loadClass(name),
            Matchers.notNullValue()
        );
    }
}
