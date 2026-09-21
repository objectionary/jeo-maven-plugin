/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link BytecodeMaxs}.
 *
 * @since 0.15
 */
final class BytecodeMaxsTest {

    @Test
    void recomputesWhenOnlyStackIsUndefined() {
        MatcherAssert.assertThat(
            "Maxs with undefined stack and defined locals must be recomputed",
            new BytecodeMaxs(-1, 4).compute(),
            Matchers.is(true)
        );
    }

    @Test
    void recomputesWhenOnlyLocalsAreUndefined() {
        MatcherAssert.assertThat(
            "Maxs with defined stack and undefined locals must be recomputed",
            new BytecodeMaxs(2, -1).compute(),
            Matchers.is(true)
        );
    }

    @Test
    void keepsFullyDefinedMaxs() {
        MatcherAssert.assertThat(
            "Maxs with both values defined must not be recomputed",
            new BytecodeMaxs(2, 4).compute(),
            Matchers.is(false)
        );
    }
}
