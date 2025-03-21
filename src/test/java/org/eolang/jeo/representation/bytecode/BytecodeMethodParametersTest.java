/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
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
            XhtmlMatchers.hasXPaths(
                "/o[contains(@base,'params')]",
                "/o[contains(@base,'params')]/o[contains(@base,'param') and contains(@as,'param-I-arg0-0-0')]",
                "/o[contains(@base,'params')]/o[contains(@base,'param') and contains(@as,'param-I-arg1-0-1')]"
            )
        );
    }
}
