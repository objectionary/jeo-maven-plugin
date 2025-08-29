/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.bytecode.BytecodePlainAnnotationValue;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.eolang.jeo.representation.xmir.XmlAnnotationValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesPlainAnnotationValue}.
 * @since 0.6
 */
final class DirectivesPlainAnnotationValueTest {

    @Test
    void createsPlainProperty() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create a plain property with a name and a value",
            new Xembler(
                new DirectivesPlainAnnotationValue(0, new Format(), "name", "particular name")
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("./o", "annotation-property").toXpath(),
                "./o[count(o) = 4]",
                "./o/o[2]/o[1]/o[text()='50-4C-41-49-4E']"
            )
        );
    }

    @Test
    void createsAnnotationPlainProperty() throws ImpossibleModificationException {
        final String name = "hello";
        final int value = 1;
        MatcherAssert.assertThat(
            "Incorrect annotation property for plain property",
            new XmlAnnotationValue(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesPlainAnnotationValue(0, new Format(), name, value)
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(new BytecodePlainAnnotationValue(name, value))
        );
    }
}
