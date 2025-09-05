/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodePlainAnnotationValue;
import org.eolang.jeo.representation.bytecode.BytecodeTypeAnnotation;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.TypePath;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlTypeAnnotation}.
 * @since 0.15.0
 */
final class XmlTypeAnnotationTest {

    @Test
    void parsesTypeAnnotation() throws ImpossibleModificationException {
        final BytecodeTypeAnnotation annotation = new BytecodeTypeAnnotation(
            0,
            TypePath.fromString("*"),
            "Lorg/eolang/jeo/SomeAnnotation;",
            true,
            Collections.singletonList(
                new BytecodePlainAnnotationValue("key", "value")
            )
        );
        MatcherAssert.assertThat(
            "We expect the type annotation to be parsed correctly",
            new XmlTypeAnnotation(
                new JcabiXmlNode(
                    new Xembler(annotation.directives(0, new Format())).xml()
                )
            ).bytecode(),
            Matchers.equalTo(annotation)
        );
    }
}
