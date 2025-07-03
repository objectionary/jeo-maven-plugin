/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.representation.directives.DirectivesAnnotations;
import org.eolang.jeo.representation.directives.DirectivesMethodParam;
import org.eolang.jeo.representation.directives.DirectivesMethodParams;
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
        MatcherAssert.assertThat(
            "Can't convert bytecode method parameters to correct directives",
            new Xembler(
                new BytecodeMethodParameters(
                    new BytecodeMethodParameter(0, Type.INT_TYPE),
                    new BytecodeMethodParameter(1, Type.INT_TYPE)
                ).directives()
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesMethodParams(
                        new DirectivesMethodParam(
                            0, "arg0", 0, Type.INT_TYPE, new DirectivesAnnotations()
                        ),
                        new DirectivesMethodParam(
                            1, "arg1", 0, Type.INT_TYPE, new DirectivesAnnotations()
                        )
                    )
                ).xml()
            )
        );
    }
}
