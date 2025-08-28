/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesArrayAnnotationValue}.
 * This class verifies the generation of array annotation value directives,
 * ensuring proper handling of array properties and conversions.
 *
 * @since 0.6.0
 */
final class DirectivesArrayAnnotationValueTest {

    @Test
    void createsArrayProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create an array property with a name",
            new Xembler(
                new DirectivesArrayAnnotationValue(
                    new Format(),
                    "name",
                    Collections.singletonList(new DirectivesPlainAnnotationValue())
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", "annotation-property").toXpath(),
                "./o/o[2]/o[1]/o[text()='41-52-52-41-59']"
            )
        );
    }

}
