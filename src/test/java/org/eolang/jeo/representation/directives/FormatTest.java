/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
}
