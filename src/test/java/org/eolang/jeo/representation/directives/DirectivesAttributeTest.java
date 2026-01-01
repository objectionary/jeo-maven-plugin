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
 * Test cases for {@link DirectivesAttribute}.
 * This class verifies the conversion of attributes to XMIR format,
 * ensuring proper handling of attribute objects and their properties.
 *
 * @since 0.6.0
 */
final class DirectivesAttributeTest {

    @Test
    void convertsToXmirWithoutChildren() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the attribute will be converted to Xmir as a simple object without children",
            new Xembler(new DirectivesAttribute("some", "a0")).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", "some").toXpath()
            )
        );
    }

    @Test
    void convertsToXmirWithChildren() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the attribute will be converted to Xmir with children",
            new Xembler(
                new DirectivesAttribute(
                    "children",
                    "a0",
                    new DirectivesValue(0, new Format(), "data")
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", "children").toXpath(),
                "./o/o[contains(@base, 'string')]"
            )
        );
    }
}
