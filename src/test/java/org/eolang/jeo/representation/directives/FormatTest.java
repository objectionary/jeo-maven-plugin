/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link Format}.
 *
 * @since 0.14.0
 */
final class FormatTest {

    @Test
    void appendsFormatsWithoutChangingPrevious() {
        MatcherAssert.assertThat(
            "Pretty property should remain unchanged",
            new Format(new Format(Format.PRETTY, false), Format.COMMENTS, true).pretty(),
            Matchers.is(false)
        );
    }

    @Test
    void appendsWithAddingNew() {
        MatcherAssert.assertThat(
            "Comments property is added successfully",
            new Format(new Format(Format.PRETTY, false), Format.COMMENTS, true).comments(),
            Matchers.is(true)
        );
    }

    @Test
    void usesDebugModeByDefault() {
        MatcherAssert.assertThat(
            "The default mode must keep the debug information, as the mojo does",
            new Format().mode(),
            Matchers.equalTo("debug")
        );
    }

    @Test
    void overridesListingWithoutChangingOriginalFormat() {
        final Format original = new Format(Format.LISTING, "nop", Format.WITH_LISTING, false);
        final Format copy = new Format(
            original, Format.LISTING, "return", Format.WITH_LISTING, true
        );
        Assertions.assertEquals("return", copy.listing());
        Assertions.assertTrue(copy.withListing());
        Assertions.assertEquals("nop", original.listing());
        Assertions.assertFalse(original.withListing());
    }

    @Test
    void rejectsPropertyWithoutValue() {
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> new Format(Format.LISTING)
        );
    }

    @Test
    void rejectsNonStringPropertyName() {
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> new Format(1, "value")
        );
    }

    @Test
    void rejectsStringWhereListingFlagIsExpected() {
        final Format format = new Format(Format.WITH_LISTING, "true");
        Assertions.assertThrows(IllegalArgumentException.class, format::withListing);
    }

    @Test
    void rejectsBooleanWhereListingTextIsExpected() {
        final Format format = new Format(Format.LISTING, true);
        Assertions.assertThrows(IllegalArgumentException.class, format::listing);
    }
}
