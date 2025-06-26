/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.asm.DisassembleMode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link BytecodeRepresentation} with omitListings parameter.
 * @since 0.9.0
 */
final class BytecodeRepresentationOmitListingsTest {

    @Test
    void omitsListingsWhenFlagIsTrue() throws Exception {
        final XML xml = new BytecodeRepresentation(
            new ResourceOf("MethodByte.class")
        ).toEO(DisassembleMode.SHORT, true);
        
        final String listingContent = xml.xpath("//listing/text()").get(0);
        MatcherAssert.assertThat(
            "Listing should contain line count summary when omitListings is true",
            listingContent,
            Matchers.matchesPattern("\\d+ lines of Bytecode")
        );
    }

    @Test
    void includesFullListingWhenFlagIsFalse() throws Exception {
        final XML xml = new BytecodeRepresentation(
            new ResourceOf("MethodByte.class")
        ).toEO(DisassembleMode.SHORT, false);
        
        final String listingContent = xml.xpath("//listing/text()").get(0);
        MatcherAssert.assertThat(
            "Listing should contain full bytecode when omitListings is false",
            listingContent,
            Matchers.containsString("org/eolang/jeo/MethodByte")
        );
        MatcherAssert.assertThat(
            "Listing should not be just a line count summary",
            listingContent,
            Matchers.not(Matchers.matchesPattern("\\d+ lines of Bytecode"))
        );
    }
}