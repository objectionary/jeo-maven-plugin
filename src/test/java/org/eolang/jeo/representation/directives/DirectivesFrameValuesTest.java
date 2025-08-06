/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Tests for {@link DirectivesFrameValues}.
 * @since 0.14.0
 */
final class DirectivesFrameValuesTest {

    @Test
    void mapsIntegersToAliases() throws ImpossibleModificationException {
        final String name = "values";
        MatcherAssert.assertThat(
            "We expect the integers to be mapped to their aliases",
            new Xembler(
                new DirectivesFrameValues(
                    name,
                    new Object[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
                )
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesValues(
                        name,
                        "top",
                        "integer",
                        "float",
                        "double",
                        "long",
                        "null",
                        "uninit_this",
                        "object",
                        "uninit",
                        9
                    )
                ).xml()
            )
        );
    }
}
