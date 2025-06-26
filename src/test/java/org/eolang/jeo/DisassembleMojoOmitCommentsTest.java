/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.project.MavenProject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link DisassembleMojo} omitComments functionality.
 * @since 0.9.0
 */
final class DisassembleMojoOmitCommentsTest {

    @Test
    void omitsCommentsWhenParameterIsTrue(@TempDir final Path temp) throws Exception {
        final Path source = temp.resolve("classes");
        final Path target = temp.resolve("target");
        Files.createDirectories(source);
        Files.createDirectories(target);
        
        // Copy a test class file to the source directory
        Files.copy(
            DisassembleMojoOmitCommentsTest.class.getResourceAsStream("/MethodByte.class"),
            source.resolve("MethodByte.class")
        );
        
        final MavenProject project = new MavenProject();
        project.setGroupId("test");
        project.setArtifactId("test");
        project.setVersion("1.0");
        
        final DisassembleMojo mojo = new DisassembleMojo();
        mojo.setProject(project);
        mojo.setSourcesDir(source.toFile());
        mojo.setOutputDir(target.toFile());
        mojo.setMode("short");
        mojo.setOmitComments(true);
        mojo.execute();
        
        final Path xmir = target.resolve("org/eolang/jeo/MethodByte.xmir");
        MatcherAssert.assertThat(
            "XMIR file should be created",
            Files.exists(xmir),
            Matchers.is(true)
        );
        
        final String content = new String(Files.readAllBytes(xmir));
        MatcherAssert.assertThat(
            "XMIR should not contain comments when omitComments is true",
            content,
            Matchers.not(Matchers.containsString("<!--"))
        );
    }

    @Test
    void includesCommentsWhenParameterIsFalse(@TempDir final Path temp) throws Exception {
        final Path source = temp.resolve("classes");
        final Path target = temp.resolve("target");
        Files.createDirectories(source);
        Files.createDirectories(target);
        
        // Copy a test class file to the source directory
        Files.copy(
            DisassembleMojoOmitCommentsTest.class.getResourceAsStream("/MethodByte.class"),
            source.resolve("MethodByte.class")
        );
        
        final MavenProject project = new MavenProject();
        project.setGroupId("test");
        project.setArtifactId("test");
        project.setVersion("1.0");
        
        final DisassembleMojo mojo = new DisassembleMojo();
        mojo.setProject(project);
        mojo.setSourcesDir(source.toFile());
        mojo.setOutputDir(target.toFile());
        mojo.setMode("short");
        mojo.setOmitComments(false);
        mojo.execute();
        
        final Path xmir = target.resolve("org/eolang/jeo/MethodByte.xmir");
        MatcherAssert.assertThat(
            "XMIR file should be created",
            Files.exists(xmir),
            Matchers.is(true)
        );
        
        final String content = new String(Files.readAllBytes(xmir));
        MatcherAssert.assertThat(
            "XMIR should contain comments when omitComments is false",
            content,
            Matchers.containsString("<!--")
        );
    }
}