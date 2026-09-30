/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link AssembleMojo}.
 *
 * @since 0.18.0
 */
final class AssembleMojoTest {

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
}
