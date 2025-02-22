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
 * Test case for {@link DirectivesAttribute}.
 * @since 0.6
 */
final class DirectivesAttributeTest {

    @Test
    void convertsToXmirWithoutChildren() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the attribute will be converted to Xmir as a simple object without children",
            new Xembler(new DirectivesAttribute("some")).xml(),
            XhtmlMatchers.hasXPaths("./o[contains(@base, 'some')]")
        );
    }

    @Test
    void convertsToXmirWithChildren() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the attribute will be converted to Xmir with children",
            new Xembler(
                new DirectivesAttribute(
                    "children",
                    new DirectivesValue("data")
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[contains(@base, 'children')]",
                "./o[contains(@base, 'children')]/o[contains(@base, 'string')]"
            )
        );
    }
}
