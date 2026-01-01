/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.TypeReference;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesTypeAnnotationTest}.
 * @since 0.15.0
 */
final class DirectivesTypeAnnotationTest {

    @Test
    void translatesToCorrectXmir() throws ImpossibleModificationException {
        final Format format = new Format();
        MatcherAssert.assertThat(
            "We expect the directives type annotation to be converted to correct XMIR",
            new Xembler(
                new DirectivesTypeAnnotation(
                    format,
                    42,
                    TypeReference.FIELD,
                    "*",
                    "LAnn;",
                    true,
                    Collections.singletonList(
                        new DirectivesPlainAnnotationValue(
                            0,
                            format,
                            "key",
                            "value"
                        )
                    )
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[@name='type-annotation-42']",
                new JeoBaseXpath("./o[@name='type-annotation-42']", "seq.of5").toXpath(),
                "./o/o[contains(@base,'number') and @name='v0']/o/o[ text()='40-33-00-00-00-00-00-00']",
                "./o/o[contains(@base,'string') and @name='v1']/o/o[ text()='2A-']",
                "./o/o[contains(@base,'string') and @name='v2']/o/o[ text()='4C-41-6E-6E-3B']",
                "./o/o[contains(@base,'true') and @name='v3']",
                "./o/o[contains(@name,'p0')]"
            )
        );
    }
}
