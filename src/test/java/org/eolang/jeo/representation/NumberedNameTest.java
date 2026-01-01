/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test cases for {@link NumberedName}.
 * This class verifies the functionality of numbered name generation,
 * ensuring proper handling of name uniqueness and numbering schemes.
 *
 * @since 0.9.0
 */
final class NumberedNameTest {

    @ParameterizedTest
    @CsvSource({
        "foo, 1, foo",
        "foo, 2, foo-2",
        "foo, 3, foo-3"
    })
    void formatsName(final String name, final int number, final String expected) {
        MatcherAssert.assertThat(
            String.format(
                "Incorrectly formatted name, expected %s, got %s",
                expected,
                new NumberedName(number, name)
            ),
            new NumberedName(number, name).toString(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void throwsExceptionWhenNumberIsLessThanOne() {
        MatcherAssert.assertThat(
            "We expect that exception message will be human-readable",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new NumberedName(0, "foo").toString(),
                "Exception was not thrown"
            ).getMessage(),
            Matchers.equalTo("Number must be greater than 0, but was: 0")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "foo, foo",
        "foo-2, foo",
        "bar-3, bar",
        "foobar-4, foobar"
    })
    void decodesName(final String encoded, final String expected) {
        MatcherAssert.assertThat(
            "Incorrectly decoded name",
            new NumberedName(encoded).plain(),
            Matchers.equalTo(expected)
        );
    }
}
