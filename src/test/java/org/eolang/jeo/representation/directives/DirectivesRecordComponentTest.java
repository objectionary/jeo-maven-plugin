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
 * Test case for {@link DirectivesRecordComponent}.
 * @since 0.15.0
 */
final class DirectivesRecordComponentTest {

    @Test
    void convertsRecordComponentToDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect generated XML to contain record component",
            new Xembler(
                new DirectivesRecordComponent(
                    new Format(),
                    0,
                    "name",
                    "descr",
                    null,
                    new DirectivesAnnotations(),
                    new DirectivesTypeAnnotations()
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@name='rc0']",
                new JeoBaseXpath("./o[@name='rc0']", "record-component").toXpath()
            )
        );
    }
}
