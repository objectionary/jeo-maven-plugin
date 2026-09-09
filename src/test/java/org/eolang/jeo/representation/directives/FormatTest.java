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
    void returnsDefaultMode() {
        MatcherAssert.assertThat(
            "The default mode should be 'short'",
            new Format().mode(),
            Matchers.equalTo("short")
        );
    }

    @Test
    void returnsMode() {
        MatcherAssert.assertThat(
            "The mode should be readable from the format",
            new Format(Format.MODE, "full").mode(),
            Matchers.equalTo("full")
        );
    }

    @Test
    void returnsListing() {
        MatcherAssert.assertThat(
            "The listing should be readable from the format",
            new Format(Format.LISTING, "listing-content").listing(),
            Matchers.equalTo("listing-content")
        );
    }

    @Test
    void returnsWithListing() {
        MatcherAssert.assertThat(
            "The with-listings flag should be readable from the format",
            new Format(Format.WITH_LISTING, true).withListing(),
            Matchers.is(true)
        );
    }

    @Test
    void rejectsNonBooleanProperty() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Format(Format.PRETTY, "yes").pretty(),
            "Expected an exception for a non-boolean property"
        );
    }

    @Test
    void rejectsNonStringProperty() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Format(Format.MODE, 42).mode(),
            "Expected an exception for a non-string property"
        );
    }

    @Test
    void rejectsOddNumberOfProperties() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Format(Format.MODE),
            "Expected an exception for an odd number of properties"
        );
    }

    @Test
    void rejectsNonStringPropertyName() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Format(42, "x"),
            "Expected an exception for a non-string property name"
        );
    }
}
