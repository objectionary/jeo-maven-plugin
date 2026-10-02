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
 * Test cases for {@link DirectivesDefaultValue}.
 * @since 0.15.0
 */
final class DirectivesDefaultValueTest {

    @Test
    void convertsDefaultValueToDirectives() throws ImpossibleModificationException {
        final Format format = new Format();
        final String xml = new Xembler(
            new DirectivesDefaultValue(new DirectivesValue(format, "v", 42))
        ).xml();
        MatcherAssert.assertThat(
            "We expect the default value to be represented as an annotation-defvalue object",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "annotation-default-value").toXpath(),
                "/o[contains(@name,'annotation-defvalue')]",
                "/o/o[@name='v']"
            )
        );
    }
}
