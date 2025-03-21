/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test case for {@link JeoFqn}.
 * @since 0.6
 */
final class JeoFqnTest {

    @ParameterizedTest
    @CsvSource({
        "opcode, jeo.opcode",
        "representation, jeo.representation",
        "int, jeo.int"
    })
    void convertsToDomainFqn(final String base, final String expected) {
        MatcherAssert.assertThat(
            "We expect the fqn to be converted to a correct jeo fqn",
            new JeoFqn(base).fqn(),
            Matchers.containsString(expected)
        );
    }
}
