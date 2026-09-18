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
 * Test cases for {@link DirectivesEnclosingMethod}.
 * @since 0.15.0
 */
final class DirectivesEnclosingMethodTest {

    @Test
    void convertsEnclosingMethodToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesEnclosingMethod(new Format(), "owner", "method", "(I)V")
        ).xml();
        MatcherAssert.assertThat(
            "We expect the enclosing method attribute to keep owner, name and descriptor",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "enclosing-method").toXpath(),
                "/o[contains(@name,'enclosing-method')]",
                "/o/o[@name='owner']",
                "/o/o[@name='name']",
                "/o/o[@name='descriptor']"
            )
        );
    }
}
