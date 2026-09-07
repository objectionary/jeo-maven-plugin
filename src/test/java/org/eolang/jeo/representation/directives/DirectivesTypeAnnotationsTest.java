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
 * Test cases for {@link DirectivesTypeAnnotations}.
 * @since 0.15.0
 */
final class DirectivesTypeAnnotationsTest {

    @Test
    void convertsEmptyTypeAnnotationsToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(new DirectivesTypeAnnotations()).xml();
        MatcherAssert.assertThat(
            "We expect empty type annotations to be an empty sequence",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "seq.of0").toXpath(),
                "/o[contains(@name,'type-annotations')]"
            )
        );
    }

    @Test
    void convertsTypeAnnotationsToDirectives() throws ImpossibleModificationException {
        final Format format = new Format();
        final String xml = new Xembler(
            new DirectivesTypeAnnotations(new DirectivesValue(format, "v", 42))
        ).xml();
        MatcherAssert.assertThat(
            "We expect non-empty type annotations to be a sequence of one element",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "seq.of1").toXpath(),
                "/o[contains(@name,'type-annotations')]",
                "/o/o[@name='v']"
            )
        );
    }
}
