/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.apache.maven.project.MavenProject;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.objectweb.asm.Opcodes;

/**
 * Test cases for {@link PluginStartup}.
 * This class verifies the plugin startup functionality,
 * including dynamic class loading and project initialization.
 *
 * @since 0.6.0
 */
final class PluginStartupTest {

    @Test
    void loadsClassesDynamically(@TempDir final Path dir) throws Exception {
        final String name = "SomeClassCompiledDynamically";
        Files.write(
            dir.resolve("SomeClassCompiledDynamically.class"),
            new BytecodeObject(
                new BytecodeClass(name)
                    .withConstructor(Opcodes.ACC_PUBLIC)
                    .opcode(Opcodes.ALOAD, 0)
                    .opcode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
                    .opcode(Opcodes.RETURN)
                    .up()
            ).bytecode().bytes()
        );
        final ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            new PluginStartup(new MavenProject(), dir).init();
            final ClassLoader dynamic = Thread.currentThread().getContextClassLoader();
            MatcherAssert.assertThat(
                "A dynamic classloader must replace the original and load the class",
                !original.equals(dynamic)
                    && Objects.nonNull(
                        dynamic.loadClass(name).getDeclaredConstructor().newInstance()
                    ),
                Matchers.is(true)
            );
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    @Test
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    void loadsClassesFromJars(@TempDir final Path dir) throws Exception {
        final String name = "SomeClassInJar";
        final Path jar = dir.resolve("dependency.jar");
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar))) {
            out.putNextEntry(new JarEntry("SomeClassInJar.class"));
            out.write(
                new BytecodeObject(
                    new BytecodeClass(name)
                        .withConstructor(Opcodes.ACC_PUBLIC)
                        .opcode(Opcodes.ALOAD, 0)
                        .opcode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
                        .opcode(Opcodes.RETURN)
                        .up()
                ).bytecode().bytes()
            );
            out.closeEntry();
        }
        final ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            new PluginStartup(null, jar).init();
            try (
                URLClassLoader jars = (URLClassLoader) Thread.currentThread()
                    .getContextClassLoader().getParent()
            ) {
                MatcherAssert.assertThat(
                    "A class from a dependency jar must be loadable",
                    Thread.currentThread().getContextClassLoader().loadClass(name).getName(),
                    Matchers.equalTo(name)
                );
            }
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
}
