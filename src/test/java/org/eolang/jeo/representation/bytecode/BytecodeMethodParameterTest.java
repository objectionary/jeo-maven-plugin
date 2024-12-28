/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
                "/o[contains(@base,'param') and contains(@name,'param-I-arg0-0-0')]"
            ),
            Arguments.of(
                1,
                Type.INT_TYPE,
                "/o[contains(@base,'param') and contains(@name,'param-I-arg1-0-1')]"
            ),
            Arguments.of(
                2,
                Type.DOUBLE_TYPE,
                "/o[contains(@base,'param') and contains(@name,'param-D-arg2-0-2')]"
            )
        );
    }
}
