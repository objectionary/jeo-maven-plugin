/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationAnnotationValue;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.eolang.jeo.representation.xmir.XmlAnnotationValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesAnnotationAnnotationValue}.
 * This class verifies the generation of annotation annotation value directives,
 * ensuring proper handling of nested annotations within annotations.
 *
 * @since 0.6.0
 */
final class DirectivesAnnotationAnnotationValueTest {

    @Test
    void createsAnnotationValue() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create an annotation property with a name and a descriptor",
            new Xembler(
                new DirectivesAnnotationAnnotationValue(
                    0,
                    new Format(),
                    "name",
                    Type.getDescriptor(String.class),
                    Collections.singletonList(new DirectivesPlainAnnotationValue())
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", "annotation-property").toXpath(),
                "./o/o[2]/o[1]/o[text()='41-4E-4E-4F-54-41-54-49-4F-4E']"
            )
        );
    }

    @Test
    void createsAnnotationAnnotationProperty() throws ImpossibleModificationException {
        final String name = "name";
        final String descriptor = "java/lang/Override";
        final boolean visible = true;
        MatcherAssert.assertThat(
            "Incorrect annotation property type",
            new XmlAnnotationValue(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesAnnotationAnnotationValue(
                            0,
                            new Format(),
                            name,
                            descriptor,
                            Collections.singletonList(new DirectivesAnnotation(descriptor, visible))
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeAnnotationAnnotationValue(
                    name,
                    descriptor,
                    Collections.singletonList(new BytecodeAnnotation(descriptor, visible))
                )
            )
        );
    }
}
