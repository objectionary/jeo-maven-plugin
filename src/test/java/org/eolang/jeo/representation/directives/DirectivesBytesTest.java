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
 * Test cases for {@link DirectivesBytes}.
 * This class verifies that bytes expose their payload through a named
 * attribute instead of the positional attribute of phi-calculus.
 *
 * @since 0.6
 */
final class DirectivesBytesTest {

    @Test
    void bindsPayloadWithNamedAttribute() {
        MatcherAssert.assertThat(
            "Bytes must expose their payload through the 'data' attribute",
            new Xembler(new DirectivesBytes("01-02-03")).xmlQuietly(),
            XhtmlMatchers.hasXPath(
                "/o[contains(@base,'bytes')]/o[@as='data' and text()='01-02-03']"
            )
        );
    }

    @Test
    void avoidsPositionalAttribute() {
        MatcherAssert.assertThat(
            "Bytes XMIR must not contain the positional attribute α0",
            new Xembler(new DirectivesBytes("01-02-03")).xmlQuietly(),
            Matchers.not(Matchers.containsString("α0"))
        );
    }
}
