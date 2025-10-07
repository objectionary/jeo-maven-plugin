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
 * Test cases for {@link EoFqn}.
 * This class verifies the conversion of base names to fully qualified EO domain names,
 * ensuring proper naming conventions for EO objects.
 *
 * @since 0.6.0
 */
final class EoFqnTest {

    @ParameterizedTest
    @CsvSource({
        "seq, Φ.org.eolang.seq",
        "bytes, Φ.org.eolang.bytes",
        "math, Φ.org.eolang.math"
    })
    void convertsToDomainFqn(final String base, final String expected) {
        MatcherAssert.assertThat(
            "We expect the fqn to be converted to a correct eo fqn",
            new EoFqn(base).fqn(),
            Matchers.equalTo(expected)
        );
    }

}
