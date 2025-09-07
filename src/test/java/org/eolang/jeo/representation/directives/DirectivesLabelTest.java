/*
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
 * Test case for {@link DirectivesLabel}.
 * @since 0.12.0
 */
final class DirectivesLabelTest {

    @Test
    void createsCorrectXmlLabel() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Expected XML to contain a label directive with the identifier 'test-label'",
            new Xembler(new DirectivesLabel(0, new Format(), "test-label")).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "label").toXpath()
            )
        );
    }
}
