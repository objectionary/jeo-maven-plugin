/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link BytecodeMethodProperties}.
 * @since 0.6
 */
final class BytecodeMethodPropertiesTest {

    @Test
    void combinesAccessModifiersWithBitwiseOr() {
        final String name = "foo";
        final String descriptor = "()V";
        final int first = 3;
        final int second = 5;
        MatcherAssert.assertThat(
            "Access modifiers must be combined with a bitwise OR, not addition",
            new BytecodeMethodProperties(name, descriptor, "", first, second),
            Matchers.equalTo(
                new BytecodeMethodProperties(first | second, name, descriptor, "")
            )
        );
    }
}
