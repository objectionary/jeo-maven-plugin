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
                "/o[@name='a0']/o[@base='Φ.jeo.unknown-attribute' and @name='φ']",
                "/o[@name='a0']/o[@base='Φ.org.eolang.string' and @name='type']",
                "/o[@name='a0']/o[@base='Φ.org.eolang.string' and @name='type']/o[@base='Φ.org.eolang.bytes']",
                "/o[@name='a0']/o[@base='Φ.org.eolang.string' and @name='type']/o[@base='Φ.org.eolang.bytes']/o[text()='53-6F-6D-65-2D-54-79-70-65']",
                "/o[@name='a0']/o[@name='data']",
                "/o[@name='a0']/o[@name='data']/o[@base='Φ.jeo.bytes' and @name='φ']",
                "/o[@name='a0']/o[@name='data']/o[@base='Φ.org.eolang.bytes' and @name='j0']",
                "/o[@name='a0']/o[@name='data']/o[@base='Φ.org.eolang.bytes' and @name='j0']/o[text()='00-01-02-03-04-05']"
            )
        );
    }
}
