/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.project.MavenProject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link DisassembleMojo} omitListings parameter.
 * @since 0.9.0
 */
final class DisassembleMojoOmitListingsTest {

    @Test
    void omitsListingsWhenParameterIsTrue(@TempDir final Path temp) throws Exception {
        final Path classes = temp.resolve("classes");
        final Path output = temp.resolve("output");
        Files.createDirectories(classes);
        Files.createDirectories(output);
        
        // Copy a test class file
        Files.copy(
            Paths.get("src/test/resources/MethodByte.class"),
            classes.resolve("MethodByte.class")
        );
        
        final DisassembleMojo mojo = new DisassembleMojo();
        this.setPrivateField(mojo, "project", new MavenProject());
        this.setPrivateField(mojo, "sourcesDir", classes.toFile());
        this.setPrivateField(mojo, "outputDir", output.toFile());
        this.setPrivateField(mojo, "disabled", false);
        this.setPrivateField(mojo, "mode", "short");
        this.setPrivateField(mojo, "xmirVerification", false);
        this.setPrivateField(mojo, "omitListings", true);
        
        mojo.execute();
        
        final Path xmirFile = output.resolve("org/eolang/jeo/MethodByte.xmir");
        MatcherAssert.assertThat(
            "XMIR file should be created",
            Files.exists(xmirFile),
            Matchers.is(true)
        );
        
        final String content = Files.readString(xmirFile);
        MatcherAssert.assertThat(
            "XMIR should contain omitted listing when omitListings is true",
            content,
            Matchers.containsString(" lines of Bytecode")
        );
        MatcherAssert.assertThat(
            "XMIR should not contain detailed bytecode when omitListings is true",
            content,
            Matchers.not(Matchers.containsString("// class version"))
        );
    }

    @Test
    void includesFullListingWhenParameterIsFalse(@TempDir final Path temp) throws Exception {
        final Path classes = temp.resolve("classes");
        final Path output = temp.resolve("output");
        Files.createDirectories(classes);
        Files.createDirectories(output);
        
        // Copy a test class file
        Files.copy(
            Paths.get("src/test/resources/MethodByte.class"),
            classes.resolve("MethodByte.class")
        );
        
        final DisassembleMojo mojo = new DisassembleMojo();
        this.setPrivateField(mojo, "project", new MavenProject());
        this.setPrivateField(mojo, "sourcesDir", classes.toFile());
        this.setPrivateField(mojo, "outputDir", output.toFile());
        this.setPrivateField(mojo, "disabled", false);
        this.setPrivateField(mojo, "mode", "short");
        this.setPrivateField(mojo, "xmirVerification", false);
        this.setPrivateField(mojo, "omitListings", false);
        
        mojo.execute();
        
        final Path xmirFile = output.resolve("org/eolang/jeo/MethodByte.xmir");
        MatcherAssert.assertThat(
            "XMIR file should be created",
            Files.exists(xmirFile),
            Matchers.is(true)
        );
        
        final String content = Files.readString(xmirFile);
        MatcherAssert.assertThat(
            "XMIR should contain full bytecode listing when omitListings is false",
            content,
            Matchers.containsString("// class version")
        );
        MatcherAssert.assertThat(
            "XMIR should not contain just line count when omitListings is false",
            content,
            Matchers.not(Matchers.containsString(" lines of Bytecode"))
        );
    }

    /**
     * Set private field using reflection.
     * @param obj Object to set field on
     * @param fieldName Name of the field
     * @param value Value to set
     */
    private void setPrivateField(final Object obj, final String fieldName, final Object value) 
        throws Exception {
        final java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}