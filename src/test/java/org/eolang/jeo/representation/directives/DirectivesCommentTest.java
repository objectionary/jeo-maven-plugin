/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesComment}.
 * This class verifies the generation of comment directives
 * and their proper XML formatting.
 *
 * @since 0.6.0
 */
final class DirectivesCommentTest {

    @Test
    void createsComment() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create proper xml a comment",
            new Xembler(
                new Directives().append(new DirectivesComment(new Format(), "Hello, world!"))
            ).xml(),
            Matchers.containsString("<!-- Hello, world! -->")
        );
    }

    @Test
    void escapesOnlyTheHyphen() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Only the hyphen must be escaped, everything else is legal comment text (see #1798)",
            new Xembler(
                new Directives().append(
                    new DirectivesComment(new Format(), "Hello -- <world> ---!")
                )
            ).xml(),
            Matchers.containsString("<!-- Hello &#45;&#45; <world> &#45;&#45;&#45;! -->")
        );
    }

    @Test
    void doesNotEscapeAmpersandAngleBracketsOrApostrophe()
        throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "An XML comment is never scanned for entities, so & < > ' need no escaping (see #1798)",
            new Xembler(
                new Directives().append(
                    new DirectivesComment(new Format(), "a&b<c>'d")
                )
            ).xml(),
            Matchers.containsString("<!-- a&b<c>'d -->")
        );
    }

    @Test
    void removesForbiddenBasicPlaneNoncharacters() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new Directives().append(
                new DirectivesComment(new Format(), "before\uFFFE\uFFFFafter")
            )
        ).xml();
        MatcherAssert.assertThat(
            "XML comments must not contain forbidden U+FFFE or U+FFFF",
            xml,
            Matchers.containsString("<!-- beforeafter -->")
        );
    }
}
