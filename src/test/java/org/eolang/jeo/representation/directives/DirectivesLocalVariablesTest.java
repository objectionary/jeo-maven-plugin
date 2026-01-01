/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
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
 * Test cases for {@link DirectivesLocalVariables}.
 * @since 0.14.0
 */
final class DirectivesLocalVariablesTest {

    @Test
    void convertsNullableSignatureToEmptyString() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We should convert null signature to empty string",
            new Xembler(
                new DirectivesLocalVariables(
                    0,
                    new Format(),
                    0,
                    "name",
                    "descriptor",
                    null,
                    new Directives(),
                    new Directives()
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                "//o[contains(@base, 'string') and contains(@name, 'signature')]/o/o[text()='--']"
            )
        );
    }

}
