/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesNumber}.
 * This class verifies the conversion of numeric values to XML format,
 * ensuring proper representation of numbers in the directive system.
 *
 * @since 0.8.0
 */
final class DirectivesNumberTest {

    @Test
    void convertsToXml() {
        MatcherAssert.assertThat(
            "We can't convert number to XML",
            new Xembler(new DirectivesNumber("42")).xmlQuietly(),
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'number')]",
                "/o[contains(@base,'number')]/o[@as='as-bytes' and contains(@base, 'bytes')]/o[@as='data' and text()='42']"
            )
        );
    }

    @Test
    void avoidsPositionalAttribute() {
        MatcherAssert.assertThat(
            "Number XMIR must not contain the positional attribute α0",
            new Xembler(new DirectivesNumber("42")).xmlQuietly(),
            Matchers.not(Matchers.containsString("α0"))
        );
    }
}
