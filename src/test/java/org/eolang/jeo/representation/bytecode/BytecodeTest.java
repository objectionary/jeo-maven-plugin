/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link org.eolang.jeo.representation.bytecode.Bytecode}.
 *
 * @since 0.1.0
 */
final class BytecodeTest {

    @Test
    void retrievesTheSameBytes() {
        final byte[] expected = {1, 2, 3};
        MatcherAssert.assertThat(
            "Bytecode should remain the same as we pass to the constructor",
            new Bytecode(expected).bytes(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void printsHumanReadableBytecodeDescription() {
        MatcherAssert.assertThat(
            "We expect correct and human-readable class description",
            new BytecodeObject(new BytecodeClass("Classname")).bytecode().toString(),
            Matchers.allOf(
                Matchers.containsString("class version"),
                Matchers.containsString("public class Classname")
            )
        );
    }
}
