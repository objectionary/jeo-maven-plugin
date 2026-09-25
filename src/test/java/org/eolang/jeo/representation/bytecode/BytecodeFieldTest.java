/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link BytecodeField}.
 *
 * @since 0.15
 */
final class BytecodeFieldTest {

    @Test
    void rejectsControlCharacterInName() {
        MatcherAssert.assertThat(
            "We expect a descriptive error for a field name that XML can't hold",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new BytecodeField(
                    String.format("bad%cname", (char) 1), "I", null, 0, Opcodes.ACC_PUBLIC
                ).directives(new Format())
            ).getMessage(),
            Matchers.containsString("U+0001")
        );
    }
}
