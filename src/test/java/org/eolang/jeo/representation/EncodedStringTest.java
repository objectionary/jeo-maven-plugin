/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test case for {@link EncodedString}.
 * @since 0.6
 */
final class EncodedStringTest {

    @ParameterizedTest
    @CsvSource({
        "Hello, Hello",
        "World, World",
        "%3Cinit%3E, <init>",
        "org%2Feolang%2Fjeo%2FMethodByte, org/eolang/jeo/MethodByte",
        "String%5B%5D, String[]"
    })
    void encodesString(final String original, final String expected) {
        MatcherAssert.assertThat(
            "Decoded string is not as expected",
            new EncodedString(original).decode(),
            Matchers.equalTo(expected)
        );
    }
}
