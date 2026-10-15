/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.concurrent.atomic.AtomicInteger;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Instruction operands captured at construction.
 *
 * @since 0.0.0
 */
final class BytecodeInstructionSnapshotTest {

    @Test
    void writesTheOriginalOperandAfterTheInputArrayChanges() {
        final Object[] arguments = {1};
        final BytecodeInstruction instruction = new BytecodeInstruction(Opcodes.BIPUSH, arguments);
        arguments[0] = 2;
        final AtomicInteger written = new AtomicInteger();
        instruction.writeTo(
            new MethodVisitor(Opcodes.ASM9) {
                @Override
                public void visitIntInsn(final int opcode, final int operand) {
                    written.set(operand);
                }
            },
            new AsmLabels()
        );
        MatcherAssert.assertThat(
            "The emitted operand changed through the caller's varargs array",
            written.get(), Matchers.equalTo(1)
        );
    }
}
