/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesMethodParams}.
 * @since 0.1
 */
final class DirectivesMethodParamsTest {

    @Test
    void convertsToNonEmptyXmirStructureEvenIfParamsAreEmpty() {
        MatcherAssert.assertThat(
            "We expect the XML to contain the 'params' element even if no parameters are defined",
            new Xembler(
                new Directives().add("o").append(new DirectivesMethodParams()).up()
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o/o[contains(@name, 'params')]")
        );
    }
}
