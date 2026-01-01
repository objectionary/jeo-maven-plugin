/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.time.DayOfWeek;
import org.eolang.jeo.representation.bytecode.BytecodeEnumAnnotationValue;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.eolang.jeo.representation.xmir.XmlAnnotationValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesEnumAnnotationValue}.
 * This class verifies the generation of enum annotation value directives,
 * ensuring proper handling of enum types in annotations.
 *
 * @since 0.6.0
 */
final class DirectivesEnumAnnotationValueTest {

    @Test
    void createsEnumProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create an enum property with a name and a value",
            new Xembler(
                new DirectivesEnumAnnotationValue(
                    0,
                    new Format(),
                    "name",
                    Type.getDescriptor(DayOfWeek.class),
                    "MONDAY"
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", "annotation-property").toXpath(),
                "./o[count(o) = 5]",
                "./o/o[2]/o[1]/o[text()='45-4E-55-4D']"
            )
        );
    }

    @Test
    void createsAnnotationEnumProperty() throws ImpossibleModificationException {
        final String name = "name";
        final String descriptor = "Lorg/eolang/jeo/representation/DataType";
        final String value = "BOOL";
        MatcherAssert.assertThat(
            "Incorrect annotation property for enum property",
            new XmlAnnotationValue(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesEnumAnnotationValue(
                            0,
                            new Format(),
                            name,
                            descriptor,
                            value
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(new BytecodeEnumAnnotationValue(name, descriptor, value))
        );
    }
}
