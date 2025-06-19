/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeArrayAnnotationValue;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.eolang.jeo.representation.xmir.XmlAnnotationValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
                    "name",
                    Collections.singletonList(new DirectivesPlainAnnotationValue())
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[contains(@base,'annotation-property')]",
                "./o[contains(@base,'annotation-property')]/o[1]/o[1]/o[text()='41-52-52-41-59']"
            )
        );
    }

    @Test
    void createsAnnotationArrayProperty() throws ImpossibleModificationException {
        final String name = "name";
        final String descriptor = "java/lang/Override";
        final boolean visible = true;
        MatcherAssert.assertThat(
            "Incorrect array annotation property",
            new XmlAnnotationValue(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesArrayAnnotationValue(
                            name,
                            Collections.singletonList(new DirectivesAnnotation(descriptor, visible))
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeArrayAnnotationValue(
                    name,
                    Collections.singletonList(new BytecodeAnnotation(descriptor, visible))
                )
            )
        );
    }
}
