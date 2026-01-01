/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
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
