/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.xembly.Xembler;

/**
 * Test for {@link DirectivesType}.
 * @since 0.11.0
 */
final class DirectivesTypeTest {

    @Test
    void convertsTypeToDirectives() {
        MatcherAssert.assertThat(
            "We expect the type to be converted to a string internally",
            new Xembler(new DirectivesType(new Format(), Type.BOOLEAN_TYPE)).xmlQuietly(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", "type").toXpath(),
                "/o/o[@base='Q.org.eolang.string']/o[@base='Q.org.eolang.bytes']/o[text()]"
            )
        );
    }
}
