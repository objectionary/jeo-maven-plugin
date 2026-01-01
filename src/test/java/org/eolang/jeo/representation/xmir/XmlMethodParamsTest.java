/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Collections;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameter;
import org.eolang.jeo.representation.bytecode.BytecodeMethodParameters;
import org.eolang.jeo.representation.bytecode.BytecodeParamAnnotations;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link XmlMethodParams}.
 * @since 0.15.0
 */
final class XmlMethodParamsTest {

    @Test
    void parsesAnnotations() throws ImpossibleModificationException {
        final int index = 0;
        final BytecodeMethodParameters original = new BytecodeMethodParameters(
            Collections.singletonList(new BytecodeMethodParameter(index, Type.INT_TYPE)),
            Collections.singletonList(
                new BytecodeParamAnnotations(
                    index,
                    new BytecodeAnnotations(
                        new BytecodeAnnotation("Ljava/lang/Deprecated;", false),
                        new BytecodeAnnotation("Lorg/jetbrains/annotations/Nullable;", true)
                    )
                )
            )
        );
        MatcherAssert.assertThat(
            "We expect to parse method parameter annotations correctly",
            new XmlMethodParams(
                new JcabiXmlNode(new Xembler(original.directives(new Format())).xml())
            ).params(),
            Matchers.equalTo(original)
        );
    }
}
