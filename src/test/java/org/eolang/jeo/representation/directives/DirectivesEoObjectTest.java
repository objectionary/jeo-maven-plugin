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
 * Test cases for {@link DirectivesEoObject}.
 * @since 0.15.0
 */
final class DirectivesEoObjectTest {

    @Test
    void convertsEoObjectToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesEoObject(
                "string",
                "name",
                new DirectivesValue(new Format(), "v", 42)
            )
        ).xml();
        MatcherAssert.assertThat(
            "We expect an EO object to carry its base and inner objects",
            xml,
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'Φ.string')]",
                "/o[@name='name']",
                "/o/o[@name='v']"
            )
        );
    }
}
