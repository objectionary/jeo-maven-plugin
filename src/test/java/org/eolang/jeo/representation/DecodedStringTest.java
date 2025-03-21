/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test case for {@link DecodedString}.
 * @since 0.6
 */
final class DecodedStringTest {

    @ParameterizedTest
    @CsvSource({
        "Hello, Hello",
        "World, World",
        "<init>, %3Cinit%3E",
        "org/eolang/jeo/MethodByte, org%2Feolang%2Fjeo%2FMethodByte",
        "String[], String%5B%5D"
    })
    void encodesString(final String original, final String expected) {
        MatcherAssert.assertThat(
            "Encoded string is not as expected",
            new DecodedString(original).encode(),
            Matchers.equalTo(expected)
        );
    }
}
