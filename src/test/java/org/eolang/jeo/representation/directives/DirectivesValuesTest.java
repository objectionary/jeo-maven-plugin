/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesValues}.
 * @since 0.6
 */
final class DirectivesValuesTest {

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that array of values will be converted to correct Xembly directives",
            new Xembler(new DirectivesValues("name", "value")).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[contains(@base,'seq.of1') and contains(@as,'name')]",
                "./o[contains(@base,'seq.of1') and contains(@as,'name')]/o/o[contains(@base, 'org.eolang.bytes')]/o[text()='76-61-6C-75-65']"
            )
        );
    }

    @RepeatedTest(100)
    void generatesRandomNameWithoutFirstDigit() {
        MatcherAssert.assertThat(
            "We expect that the name of the sequence will be generated randomly and will not start with a digit",
            new NativeXmlNode(
                new Xembler(
                    new DirectivesValues("", "some-value")
                ).xmlQuietly()
            ).attribute("as").orElseThrow(
                () -> new IllegalStateException("Name attribute is absent")
            ),
            Matchers.matchesRegex("^[^0-9].*")
        );
    }
}
