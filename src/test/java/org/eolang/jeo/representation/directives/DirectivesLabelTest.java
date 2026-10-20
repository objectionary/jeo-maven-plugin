/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesLabel}.
 *
 * @since 0.12.0
 */
final class DirectivesLabelTest {

    @Test
    void createsCorrectXmlLabel() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Expected XML to contain a label directive with the identifier 'test-label'",
            new Xembler(new DirectivesLabel(0, new Format(), "test-label")).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "label").toXpath()
            )
        );
    }

    @Test
    void carriesTheIdentifierOfTheLabel() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "The label must hold its identifier as a string value",
            new Xembler(new DirectivesLabel(0, new Format(), "test-label")).xml(),
            XhtmlMatchers.hasXPaths(
                "/o/o[@base='Φ.string']/o/o[@as='data' and text()='74-65-73-74-2D-6C-61-62-65-6C']"
            )
        );
    }

    @Test
    void writesNopWhenIdentifierIsAbsent() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "A label without identifier must become a nop",
            new Xembler(new DirectivesLabel(3, new Format(), null)).xml(),
            XhtmlMatchers.hasXPaths("/o[@base='Φ.nop' and @name='n3']")
        );
    }
}
