/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
 * Test case for {@link DirectivesComment}.
 * @since 0.6
 */
final class DirectivesCommentTest {

    @Test
    void createsComment() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create proper xml a comment",
            new Xembler(
                new Directives().append(new DirectivesComment("Hello, world!"))
            ).xml(),
            Matchers.containsString("<!-- Hello, world! -->")
        );
    }

    @Test
    void escapesUnsafeCharacters() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't escape unsafe characters",
            new Xembler(
                new Directives().append(new DirectivesComment("Hello -- <world> ---!"))
            ).xml(),
            Matchers.containsString("<!-- Hello &#45;&#45; &lt;world&gt; &#45;&#45;&#45;! -->")
        );
    }
}
