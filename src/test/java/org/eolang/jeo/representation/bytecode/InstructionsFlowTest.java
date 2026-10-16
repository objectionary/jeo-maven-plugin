/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link InstructionsFlow}.
 *
 * @since 0.18.0
 */
final class InstructionsFlowTest {

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void computesMaxsOfLongMethodQuickly() {
        final List<BytecodeEntry> entries = new ArrayList<>(40_005);
        final BytecodeLabel start = new BytecodeLabel("start");
        entries.add(start);
        for (int step = 0; step < 20_000; ++step) {
            final BytecodeLabel next = new BytecodeLabel(String.format("next%d", step));
            entries.add(new BytecodeInstruction(Opcodes.GOTO, next));
            entries.add(next);
        }
        final BytecodeLabel end = new BytecodeLabel("end");
        entries.add(end);
        entries.add(new BytecodeInstruction(Opcodes.RETURN));
        final BytecodeLabel handler = new BytecodeLabel("handler");
        entries.add(handler);
        entries.add(new BytecodeInstruction(Opcodes.ATHROW));
        MatcherAssert.assertThat(
            "Max stack of a long method must be computed in linear time",
            new BytecodeMethod(
                Collections.singletonList(
                    new BytecodeTryCatchBlock(start, end, handler, "java/lang/Exception")
                ),
                entries,
                new BytecodeAnnotations(),
                new BytecodeMethodProperties("m", "()V", Opcodes.ACC_PUBLIC),
                new ArrayList<>(0),
                new BytecodeMaxs(),
                new BytecodeAttributes()
            ).computeMaxs(),
            Matchers.equalTo(new BytecodeMaxs(1, 1))
        );
    }
}
