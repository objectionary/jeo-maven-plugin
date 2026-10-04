/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test cases for {@link InstructionsFlow}.
 *
 * @since 0.15
 */
final class InstructionsFlowTest {

    @Test
    void rejectsLoopWithGrowingStack() {
        final BytecodeLabel loop = new BytecodeLabel("loop");
        final IllegalStateException error = Assertions.assertThrows(
            IllegalStateException.class,
            () -> new MaxStack(
                Arrays.asList(
                    loop,
                    new BytecodeInstruction(Opcodes.ICONST_0),
                    new BytecodeInstruction(Opcodes.GOTO, loop)
                ),
                Collections.emptyList()
            ).value()
        );
        MatcherAssert.assertThat(
            error.getMessage(),
            Matchers.containsString("Incompatible values at instruction 0")
        );
    }
}
