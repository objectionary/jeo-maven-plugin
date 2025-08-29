/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesLine}.
 * @since 0.14.0
 */
final class DirectivesLineTest {

    @Test
    void generatesValidXmirForLineNumber() throws ImpossibleModificationException {
        final DirectivesLine directives = new DirectivesLine(0, new Format(), 42, "test");
        final String root = "./o[contains(@name,'ln')]";
        MatcherAssert.assertThat(
            "We expect to generate a valid XMIR for the line number directive",
            new Xembler(directives).xml(),
            XhtmlMatchers.hasXPaths(
                root,
                new JeoBaseXpath(root, "line-number").toXpath()
            )
        );
    }
}
