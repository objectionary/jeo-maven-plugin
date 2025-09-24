/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Arrays;
import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotationAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeArrayAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodePlainAnnotationValue;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlAnnotation}.
 * @since 0.6
 */
final class XmlAnnotationTest {

    @Test
    void throwsExceptionOnBrokenXml() {
        Assertions.assertThrows(
            ParsingException.class,
            () -> new XmlAnnotation(
                new NativeXmlNode(
                    new Xembler(
                        new Directives(
                            new BytecodeAnnotation(
                                "Lorg/eolang/jeo/representation/xmir/XmlAnnotation;",
                                true
                            ).directives(0, new Format())
                        ).xpath("./o/o").remove()
                    ).xml()
                )
            ).bytecode(),
            "Broken XML annotation should throw an exception"
        );
    }

    @Test
    void parsesAnnotationBack() throws ImpossibleModificationException {
        final String value = "value";
        final BytecodeAnnotation original = new BytecodeAnnotation(
            "Lorg/eolang/jeo/JeoSubTypes;",
            true,
            Collections.singletonList(
                new BytecodeArrayAnnotationValue(
                    value,
                    Collections.singletonList(
                        new BytecodeAnnotationAnnotationValue(
                            value,
                            "Lorg/eolang/jeo/JeoSubType;",
                            Arrays.asList(
                                new BytecodePlainAnnotationValue(value, "String.class"),
                                new BytecodePlainAnnotationValue("string", "string")
                            )
                        )
                    )
                )
            )
        );
        MatcherAssert.assertThat(
            "Failed to parse XML annotation back to bytecode",
            new XmlAnnotation(
                new JcabiXmlNode(new Xembler(original.directives(1, new Format())).xml())
            ).bytecode(),
            Matchers.equalTo(original)
        );
    }
}
