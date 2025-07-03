/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeArrayAnnotationValue;
import org.eolang.jeo.representation.directives.DirectivesAnnotation;
import org.eolang.jeo.representation.directives.DirectivesArrayAnnotationValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link XmlAnnotationValue}.
 * @since 0.11.0
 */
final class XmlAnnotationValueTest {

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
