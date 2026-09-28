/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeArrayAnnotationValue;
import org.eolang.jeo.representation.directives.DirectivesAnnotation;
import org.eolang.jeo.representation.directives.DirectivesArrayAnnotationValue;
import org.eolang.jeo.representation.directives.DirectivesPlainAnnotationValue;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link XmlAnnotationValue}.
 *
 * @since 0.11.0
 */
final class XmlAnnotationValueTest {

    @Test
    void refusesAPropertyWithTooFewValues() throws ImpossibleModificationException {
        final String xml =
            new Xembler(
                new DirectivesPlainAnnotationValue(0, new Format(), "name", "value")
            ).xml();
        final int last = xml.lastIndexOf("<o base=");
        MatcherAssert.assertThat(
            "the message must name the type and both counts",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new XmlAnnotationValue(
                    new NativeXmlNode(
                        String.join("", xml.substring(0, last), "</o>")
                    )
                ).bytecode(),
                "a PLAIN property that lost a value must be refused"
            ).getMessage(),
            Matchers.allOf(
                Matchers.containsString("has 1 values"),
                Matchers.containsString("2 are expected")
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
                            0,
                            new Format(),
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
