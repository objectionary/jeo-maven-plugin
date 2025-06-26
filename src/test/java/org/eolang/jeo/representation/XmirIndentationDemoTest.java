/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Demonstration test for XMIR indentation fix.
 * This test creates an actual XMIR file and verifies it uses two-space indentation.
 * 
 * @since 0.6.0
 */
final class XmirIndentationDemoTest {

    @Test
    void demonstratesTwoSpaceIndentationInActualFile(@TempDir final Path temp) throws IOException {
        // Create a bytecode program
        final BytecodeProgram program = new BytecodeProgram(
            new BytecodeClass("DemoClass").helloWorldMethod()
        );
        
        // Generate XMIR and write to file
        final String xmir = program.xml().toString();
        final Path xmirFile = temp.resolve("demo.xmir");
        Files.write(xmirFile, xmir.getBytes(StandardCharsets.UTF_8));
        
        // Print the generated XMIR for manual verification
        System.out.println("Generated XMIR with two-space indentation:");
        System.out.println(xmir);
        System.out.println("---");
        System.out.println("File saved to: " + xmirFile.toAbsolutePath());
        
        // Verify file exists and has content
        MatcherAssert.assertThat(
            "XMIR file should exist",
            Files.exists(xmirFile),
            Matchers.is(true)
        );
        
        MatcherAssert.assertThat(
            "XMIR file should not be empty",
            Files.size(xmirFile) > 0,
            Matchers.is(true)
        );
        
        // Verify that it contains XML content
        MatcherAssert.assertThat(
            "XMIR should contain XML declaration",
            xmir.contains("<?xml"),
            Matchers.is(true)
        );
        
        // Verify two-space indentation by checking that indented lines use 2-space multiples
        final String[] lines = xmir.split("\n");
        boolean foundTwoSpaceIndent = false;
        boolean foundThreeSpaceIndent = false;
        
        for (final String line : lines) {
            if (line.startsWith("  ") && !line.startsWith("   ")) {
                foundTwoSpaceIndent = true;
            }
            if (line.startsWith("   ") && !line.startsWith("    ")) {
                foundThreeSpaceIndent = true;
            }
        }
        
        MatcherAssert.assertThat(
            "XMIR should use two-space indentation",
            foundTwoSpaceIndent,
            Matchers.is(true)
        );
        
        MatcherAssert.assertThat(
            "XMIR should not use three-space indentation",
            foundThreeSpaceIndent,
            Matchers.is(false)
        );
    }
}