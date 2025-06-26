/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for XMIR indentation.
 * This test verifies that XMIR files are generated with two-space indentation.
 * 
 * @since 0.6.0
 */
final class XmirIndentationTest {

    @Test
    void generatesXmirWithTwoSpaceIndentation() {
        final BytecodeProgram program = new BytecodeProgram(
            new BytecodeClass("TestClass")
        );
        final String xml = program.xml().toString();
        
        // Check that the XML contains two-space indentation
        final String[] lines = xml.split("\n");
        boolean foundTwoSpaceIndentation = false;
        boolean foundThreeSpaceIndentation = false;
        
        for (final String line : lines) {
            if (line.startsWith("  ") && !line.startsWith("   ")) {
                foundTwoSpaceIndentation = true;
            }
            if (line.startsWith("   ") && !line.startsWith("    ")) {
                foundThreeSpaceIndentation = true;
            }
        }
        
        MatcherAssert.assertThat(
            "XMIR should contain two-space indentation",
            foundTwoSpaceIndentation,
            Matchers.is(true)
        );
        
        MatcherAssert.assertThat(
            "XMIR should not contain three-space indentation",
            foundThreeSpaceIndentation,
            Matchers.is(false)
        );
    }

    @Test
    void verifyNoThreeSpaceIndentationInComplexXmir() {
        final BytecodeProgram program = new BytecodeProgram(
            new BytecodeClass("ComplexClass").helloWorldMethod()
        );
        final String xml = program.xml().toString();
        
        // Use regex to find any lines that start with exactly 3 spaces
        final boolean hasThreeSpaceIndentation = xml.matches("(?s).*^   [^ ].*");
        
        MatcherAssert.assertThat(
            String.format(
                "XMIR should not contain three-space indentation. XML content:\n%s",
                xml
            ),
            hasThreeSpaceIndentation,
            Matchers.is(false)
        );
    }
}