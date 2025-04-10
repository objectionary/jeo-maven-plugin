/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link BytecodeMethodParameter}.
 * @since 0.6
 */
final class BytecodeMethodParameterTest {

    @ParameterizedTest(name = "Converts param with index {0} and type {1} to directives")
    @MethodSource("parameters")
    void convertsToDirectives(
        final int index, final Type type, final String expected
    ) throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We can't convert bytecode method parameter to correct XML directives",
            new Xembler(new BytecodeMethodParameter(index, type).directives()).xml(),
            XhtmlMatchers.hasXPaths(expected)
        );
    }

    /**
     * Test cases for {@link #convertsToDirectives(int, Type, String)} test.
     * Do not remove this method.
     * This method is used as a source of arguments for the test above.
     * @return Test cases.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parameters() {
        return Stream.of(
            Arguments.of(
                0,
                Type.INT_TYPE,
                "/o[contains(@base,'param') and contains(@as,'arg0')]"
            ),
            Arguments.of(
                1,
                Type.INT_TYPE,
                "/o[contains(@base,'param') and contains(@as,'arg1')]"
            ),
            Arguments.of(
                2,
                Type.DOUBLE_TYPE,
                "/o[contains(@base,'param') and contains(@as,'arg2')]"
            )
        );
    }
}
