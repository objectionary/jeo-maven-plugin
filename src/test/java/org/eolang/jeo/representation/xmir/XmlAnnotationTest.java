/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
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
                            ).directives()
                        ).xpath("./o/o").remove()
                    ).xml()
                )
            ).bytecode(),
            "Broken XML annotation should throw an exception"
        );
    }
}
