/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesNumber}.
 * @since 0.8
 */
final class DirectivesNumberTest {

    @Test
    void convertsToXml() {
        MatcherAssert.assertThat(
            "We can't convert number to XML",
            new Xembler(new DirectivesNumber("42")).xmlQuietly(),
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'number')]",
                "/o[contains(@base,'number')]/o[contains(@base, 'bytes') and text()='42']"
            )
        );
    }
}
