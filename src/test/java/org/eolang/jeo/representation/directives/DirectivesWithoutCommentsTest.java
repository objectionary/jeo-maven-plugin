/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesWithoutComments}.
 *
 * @since 0.11.0
 */
final class DirectivesWithoutCommentsTest {

    @Test
    void containsCommentsByDefault() {
        final DirectivesValue with = new DirectivesValue("data");
        MatcherAssert.assertThat(
            String.format("Plain directives should contain comments, but they do not %s", with),
            new Xembler(with).xmlQuietly(),
            Matchers.containsString("<!--")
        );
    }

    @Test
    void removesComments() {
        MatcherAssert.assertThat(
            "Directives without comments do not contain comments",
            new Xembler(new DirectivesWithoutComments(new DirectivesValue("data"))).xmlQuietly(),
            Matchers.not(Matchers.containsString("<!--"))
        );
    }
}
