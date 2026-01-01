/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesRecordComponents}.
 * @since 0.15.0
 */
final class DirectivesRecordComponentsTest {

    @Test
    void convertsRecordComponentsToDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect generated XML to contain record components",
            new Xembler(
                new DirectivesRecordComponents(
                    9,
                    Collections.singletonList(
                        new DirectivesRecordComponent(
                            new Format(),
                            0,
                            "name",
                            "descr",
                            null,
                            new DirectivesAnnotations(),
                            new DirectivesTypeAnnotations()
                        )
                    )
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@name='record-components-9']",
                new JeoBaseXpath(
                    "./o[@name='record-components-9']",
                    "record-components"
                ).toXpath(),
                "./o/o[@name='all']/o[@name='rc0']"
            )
        );
    }

    @Test
    void convertsEmptyRecordComponentsToDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect generated XML to be empty",
            new Xembler(
                new DirectivesRecordComponents(
                    0,
                    Collections.emptyList()
                ), new Transformers.Node()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@name='record-components-0']",
                new JeoBaseXpath(
                    "./o[@name='record-components-0']",
                    "record-components"
                ).toXpath()
            )
        );
    }
}
