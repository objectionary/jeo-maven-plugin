/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TwoSpaceXml}.
 * 
 * @since 0.6.0
 */
final class TwoSpaceXmlTest {

    @Test
    void convertsThreeSpaceToTwoSpace() {
        // Create XML with simple structure, let XMLDocument format it
        final String input = "<root><child><nested>value</nested></child></root>";
        final XMLDocument originalDoc = new XMLDocument(input);
        final String original = originalDoc.toString();
        
        // Verify original has 3-space indentation
        MatcherAssert.assertThat(
            "Original XMLDocument should use 3-space indentation",
            original.contains("   <child>"),
            Matchers.is(true)
        );
        
        final TwoSpaceXml xml = new TwoSpaceXml(originalDoc);
        final String converted = xml.toString();
        
        // Verify converted has 2-space indentation
        MatcherAssert.assertThat(
            "TwoSpaceXml should convert 3-space indentation to 2-space indentation",
            converted.contains("  <child>"),
            Matchers.is(true)
        );
        
        MatcherAssert.assertThat(
            "TwoSpaceXml should not contain 3-space indentation",
            converted.contains("   <child>"),
            Matchers.is(false)
        );
    }

    @Test
    void preservesContentWithoutIndentation() {
        final String input = "<root><child>value</child></root>";
        final XMLDocument originalDoc = new XMLDocument(input);
        
        final TwoSpaceXml xml = new TwoSpaceXml(originalDoc);
        final String converted = xml.toString();
        
        MatcherAssert.assertThat(
            "TwoSpaceXml should preserve basic XML structure",
            converted.contains("<root>"),
            Matchers.is(true)
        );
        
        MatcherAssert.assertThat(
            "TwoSpaceXml should preserve XML content",
            converted.contains("<child>value</child>"),
            Matchers.is(true)
        );
    }

    @Test
    void preservesXPathFunctionality() {
        final String input = "<root><child name=\"test\">value</child></root>";
        
        final TwoSpaceXml xml = new TwoSpaceXml(new XMLDocument(input));
        MatcherAssert.assertThat(
            "TwoSpaceXml should preserve XPath functionality",
            xml.xpath("/root/child/@name"),
            Matchers.contains("test")
        );
    }
}