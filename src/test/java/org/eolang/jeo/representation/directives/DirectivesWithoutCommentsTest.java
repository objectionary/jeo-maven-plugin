/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directive;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesWithoutComments}.
 * @since 0.11.0
 */
final class DirectivesWithoutCommentsTest {

    @Test
    void removesComments() {
        final DirectivesValue with = new DirectivesValue("data");
        final Iterable<Directive> without = new DirectivesWithoutComments(with);
        MatcherAssert.assertThat(
            String.format("Plain directives should contain comments, but they do not %s", with),
            new Xembler(with).xmlQuietly(),
            Matchers.containsString("<!--")
        );
        MatcherAssert.assertThat(
            "Directives without comments do not contain comments",
            new Xembler(without).xmlQuietly(),
            Matchers.not(Matchers.containsString("<!--"))
        );
    }
}
