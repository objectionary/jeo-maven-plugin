/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test case for {@link EoFqn}.
 * @since 0.6
 */
final class EoFqnTest {

    @ParameterizedTest
    @CsvSource({
        "seq, Q.org.eolang.seq",
        "bytes, Q.org.eolang.bytes",
        "math, Q.org.eolang.math"
    })
    void convertsToDomainFqn(final String base, final String expected) {
        MatcherAssert.assertThat(
            "We expect the fqn to be converted to a correct eo fqn",
            new EoFqn(base).fqn(),
            Matchers.equalTo(expected)
        );
    }

}
