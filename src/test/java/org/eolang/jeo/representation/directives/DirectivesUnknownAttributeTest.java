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
 * Test case for {@link DirectivesUnknownAttribute}.
 * @since 0.15.0
 */
final class DirectivesUnknownAttributeTest {

    @Test
    void convertsToCorrectXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "All xpaths must match",
            new Xembler(
                new DirectivesUnknownAttribute(
                    new Format(),
                    0,
                    "Some-Type",
                    new byte[]{0, 1, 2, 3, 4, 5}
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[@name='a0']",
                "/o[@name='a0']/o[@base='Q.jeo.unknown-attribute' and @name='@']",
                "/o[@name='a0']/o[@base='Q.org.eolang.string' and @name='type']",
                "/o[@name='a0']/o[@base='Q.org.eolang.string' and @name='type']/o[@base='Q.org.eolang.bytes']",
                "/o[@name='a0']/o[@base='Q.org.eolang.string' and @name='type']/o[@base='Q.org.eolang.bytes']/o[text()='53-6F-6D-65-2D-54-79-70-65']",
                "/o[@name='a0']/o[@name='data']",
                "/o[@name='a0']/o[@name='data']/o[@base='Q.jeo.bytes' and @name='@']",
                "/o[@name='a0']/o[@name='data']/o[@base='Q.org.eolang.bytes' and @name='j0']",
                "/o[@name='a0']/o[@name='data']/o[@base='Q.org.eolang.bytes' and @name='j0']/o[text()='00-01-02-03-04-05']"
            )
        );
    }
}
