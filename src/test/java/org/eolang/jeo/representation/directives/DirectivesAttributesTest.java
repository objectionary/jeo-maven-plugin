/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesAttributes}.
 * @since 0.6
 */
final class DirectivesAttributesTest {

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that attributes will be converted to Xmir as a sequence of two objects with the name 'attributes'",
            new Xembler(
                new DirectivesAttributes(
                    new DirectivesAttribute("first"),
                    new DirectivesAttribute("second")
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[contains(@as, 'attributes') and contains(@base, 'seq.of2')]",
                "./o[contains(@base, 'seq.of2')]/o[contains(@base, 'first')]",
                "./o[contains(@base, 'seq.of2')]/o[contains(@base, 'second')]"
            )
        );
    }
}
