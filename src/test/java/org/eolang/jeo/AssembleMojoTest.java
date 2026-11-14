/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.maven.plugin.MojoExecutionException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link AssembleMojo}.
 *
 * @since 0.15.0
 */
final class AssembleMojoTest {

    @Test
    void rejectsThreadsAboveLimitBeforeAnyWork() throws Exception {
        MatcherAssert.assertThat(
            "A thread count above the ForkJoinPool limit must be rejected with a message naming the parameter",
            Assertions.assertThrows(
                MojoExecutionException.class,
                () -> AssembleMojoTest.mojoWithThreads(40_000).execute()
            ).getMessage(),
            Matchers.allOf(
                Matchers.containsString("jeo.assemble.threads"),
                Matchers.containsString("between 0 and 32767")
            )
        );
    }

    @Test
    @SuppressWarnings({"PMD.AvoidAccessibilityAlteration", "PMD.UnnecessaryLocalRule"})
    void installsProjectClassLoaderEvenWithoutVerification(@TempDir final Path temp)
        throws Exception {
        final AssembleMojo mojo = new AssembleMojo();
        final Field sources = AssembleMojo.class.getDeclaredField("sourcesDir");
        sources.setAccessible(true);
        sources.set(mojo, Files.createDirectories(temp.resolve("xmir")).toFile());
        final Field output = AssembleMojo.class.getDeclaredField("outputDir");
        output.setAccessible(true);
        output.set(mojo, Files.createDirectories(temp.resolve("classes")).toFile());
        final Field skip = AssembleMojo.class.getDeclaredField("skipVerification");
        skip.setAccessible(true);
        skip.set(mojo, true);
        final ClassLoader before = Thread.currentThread().getContextClassLoader();
        try {
            mojo.execute();
            MatcherAssert.assertThat(
                "The project class loader must be installed for the assembly itself",
                Thread.currentThread().getContextClassLoader(),
                Matchers.instanceOf(JeoClassLoader.class)
            );
        } finally {
            Thread.currentThread().setContextClassLoader(before);
        }
    }

    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    private static AssembleMojo mojoWithThreads(final int threads) throws Exception {
        final AssembleMojo mojo = new AssembleMojo();
        final Field field = AssembleMojo.class.getDeclaredField("threads");
        field.setAccessible(true);
        field.set(mojo, threads);
        return mojo;
    }
}
