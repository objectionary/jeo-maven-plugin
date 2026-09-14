/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.List;

/**
 * Bytecode method max stack.
 * This class knows hot to compute the maximum size of the stack
 * that can be used by a method.
 *
 * @since 0.6
 */
final class MaxStack {

    /**
     * Method instructions.
     */
    private final List<? extends BytecodeEntry> instructions;

    /**
     * Try-catch blocks.
     */
    private final List<BytecodeTryCatchBlock> blocks;

    /**
     * Compute the maximum stack size.
     *
     * @param instructions Instructions
     * @param catches Try-catch blocks
     */
    MaxStack(
        final List<? extends BytecodeEntry> instructions,
        final List<BytecodeTryCatchBlock> catches
    ) {
        this.instructions = instructions;
        this.blocks = catches;
    }

    /**
     * Compute the maximum stack size.
     *
     * @return Maximum stack size
     */
    int value() {
        return new InstructionsFlow<Stack>(this.instructions, this.blocks)
            .max(new Stack(0), Stack::new)
            .orElse(new Stack(0))
            .integer();
    }
}
