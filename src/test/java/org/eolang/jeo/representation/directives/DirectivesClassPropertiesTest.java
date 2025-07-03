/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link org.eolang.jeo.representation.directives.DirectivesClassProperties}.
 * @since 0.1.0
 */
final class DirectivesClassPropertiesTest {

    @Test
    void createsDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create proper xml",
            new Xembler(
                new Directives()
                    .add("o")
                    .append(
                        new DirectivesClassProperties(
                            1,
                            "org/eolang/SomeClass",
                            "java/lang/Object",
                            "org/eolang/SomeInterface"
                        )
                    ).up()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o/o[contains(@base,'number') and contains(@as,'version')]",
                "/o/o[contains(@base,'number') and contains(@as,'access')]",
                "/o/o[contains(@base,'string') and contains(@as,'signature')]",
                "/o/o[contains(@base,'string') and contains(@as,'supername')]",
                new JeoBaseXpath("./o/o", "seq.of1").toXpath(),
                "/o/o[contains(@as,'interfaces')]"
            )
        );
    }
}
