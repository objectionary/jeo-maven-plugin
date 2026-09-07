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
 * Test cases for {@link DirectivesMaxs}.
 * @since 0.15.0
 */
final class DirectivesMaxsTest {

    @Test
    void convertsMaxsToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(new DirectivesMaxs(new Format(), 1, 2)).xml();
        MatcherAssert.assertThat(
            "We expect max stack and max locals to be represented",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "maxs").toXpath(),
                "/o[contains(@name,'maxs')]",
                "/o/o[@name='m1']",
                "/o/o[@name='m2']"
            )
        );
    }

    @Test
    void convertsUndefinedMaxsToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(new DirectivesMaxs()).xml();
        MatcherAssert.assertThat(
            "We expect undefined max stack and max locals to be represented",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "maxs").toXpath(),
                "/o[contains(@name,'maxs')]",
                "/o/o[@name='m1']",
                "/o/o[@name='m2']"
            )
        );
    }
}
