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
 * Test cases for {@link DirectivesGlobalObject}.
 * @since 0.8
 */
final class DirectivesGlobalObjectTest {

    @Test
    void convertsGlobalObjectToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesGlobalObject(
                "class",
                "j$Foo",
                new DirectivesValue(new Format(), "v", 42)
            )
        ).xml();
        MatcherAssert.assertThat(
            "We expect a global object to carry its base, name and inner objects",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "class").toXpath(),
                "/o[contains(@name,'j$Foo')]",
                "/o/o[@name='v']"
            )
        );
    }
}
