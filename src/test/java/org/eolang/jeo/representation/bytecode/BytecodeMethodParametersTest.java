/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Collections;
import org.eolang.jeo.representation.directives.DirectivesAnnotation;
import org.eolang.jeo.representation.directives.DirectivesAnnotations;
import org.eolang.jeo.representation.directives.DirectivesMethodParam;
import org.eolang.jeo.representation.directives.DirectivesMethodParams;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Bytecode method parameters.
 * @since 0.6
 */
final class BytecodeMethodParametersTest {

    @Test
    void convertsToDirectivesWithTwoParams() throws ImpossibleModificationException {
        final Format format = new Format();
        MatcherAssert.assertThat(
            "Can't convert bytecode method parameters to correct directives",
            new Xembler(
                new BytecodeMethodParameters(
                    new BytecodeMethodParameter(0, Type.INT_TYPE),
                    new BytecodeMethodParameter(1, Type.INT_TYPE)
                ).directives(format)
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesMethodParams(
                        new DirectivesMethodParam(
                            format, 0, null, 0, Type.INT_TYPE
                        ),
                        new DirectivesMethodParam(
                            format, 1, null, 0, Type.INT_TYPE
                        )
                    )
                ).xml()
            )
        );
    }

    @Test
    void convertsMethodParamAnnotationsToDirectives() throws ImpossibleModificationException {
        final String descriptor = "Ljava/lang/Deprecated()";
        final boolean visible = true;
        final Format format = new Format();
        final int index = 0;
        MatcherAssert.assertThat(
            "We expect a visible annotation to be converted to xmir",
            new Xembler(
                new BytecodeMethodParameters(
                    new ArrayList<>(index),
                    Collections.singletonList(
                        new BytecodeParamAnnotations(
                            index,
                            new BytecodeAnnotations(
                                new BytecodeAnnotation(descriptor, visible)
                            )
                        )
                    )
                ).directives(format)
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesMethodParams(
                        Collections.emptyList(),
                        new DirectivesAnnotations(
                            Collections.singletonList(
                                new DirectivesAnnotation(index, format, descriptor, visible)
                            ),
                            "param-annotations-0"
                        )
                    )
                ).xml()
            )
        );
    }
}
