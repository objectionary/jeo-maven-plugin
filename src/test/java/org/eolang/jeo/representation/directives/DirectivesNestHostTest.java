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
 * Test cases for {@link DirectivesNestHost}.
 * @since 0.15.0
 */
final class DirectivesNestHostTest {

    @Test
    void convertsNestHostToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesNestHost(new Format(), "com/example/Host")
        ).xml();
        MatcherAssert.assertThat(
            "We expect the nest host attribute to contain the host name",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "nest-host").toXpath(),
                "/o[contains(@name,'nest-host')]",
                "/o/o[@name='host']"
            )
        );
    }
}
