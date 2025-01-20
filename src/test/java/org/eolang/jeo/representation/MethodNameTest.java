/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
package org.eolang.jeo.representation;

import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test for {@link MethodName}.
 * @since 0.6
 */
final class MethodNameTest {

    @ParameterizedTest(name = "Converts \"{1}\" to \"{0}\"")
    @MethodSource("names")
    void convertsToBytecode(final String bytecode, final String xmir) {
        MatcherAssert.assertThat(
            "Converted bytecode should be correct",
            new MethodName(xmir).bytecode(),
            Matchers.equalTo(bytecode)
        );
    }

    @ParameterizedTest(name = "Converts \"{0}\" to \"{1}\"")
    @MethodSource("names")
    void convertsToXmir(final String bytecode, final String xmir) {
        MatcherAssert.assertThat(
            "Converted xmir should be correct",
            new MethodName(bytecode).xmir(),
            Matchers.equalTo(xmir)
        );
    }

    /**
     * Test cases for different tests.
     * Method is used by {@link #convertsToBytecode(String, String)}
     * and {@link #convertsToXmir(String, String)}.
     * @return Test cases.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> names() {
        return Stream.of(
            Arguments.of("<init>", "object@init@"),
            Arguments.of("<clinit>", "class@clinit@"),
            Arguments.of("main", "main"),
            Arguments.of("init", "init"),
            Arguments.of("new", "new")
        );
    }

}
