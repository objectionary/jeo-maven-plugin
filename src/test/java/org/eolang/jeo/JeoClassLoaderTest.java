/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.eolang.jeo.representation.bytecode.JavaSourceClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link JeoClassLoader}.
 *
 * @since 0.6.0
 */
final class JeoClassLoaderTest {

    @Test
    void loadsAClassFromAPackageNamedClasses(@TempDir final Path temp)
        throws IOException, ClassNotFoundException {
        final Path pckg = temp.resolve("com").resolve("acme").resolve("classes");
        Files.createDirectories(pckg);
        Files.write(
            pckg.resolve("Foo.class"),
            new JavaSourceClass("org/eolang/jeo/loader/Foo.java").compile().bytes()
        );
        MatcherAssert.assertThat(
            "a class from a package containing 'classes' must be found by its real name",
            new JeoClassLoader(
                Thread.currentThread().getContextClassLoader(),
                Collections.singleton(temp.toString())
            ).loadClass("com.acme.classes.Foo").getName(),
            Matchers.equalTo("com.acme.classes.Foo")
        );
    }
}
