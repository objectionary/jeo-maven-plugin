/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.List;
import lombok.ToString;

/**
 * Bytecode method max stack.
 * This class knows hot to compute the maximum size of the stack
 * that can be used by a method.
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
     * @param instructions Instructions.
     * @param catches Try-catch blocks.
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
     * @return Maximum stack size.
     */
    public int value() {
        return new InstructionsFlow<Stack>(this.instructions, this.blocks)
            .max(new Stack(0), Stack::new)
            .orElse(new Stack(0))
            .integer();
    }

    /**
     * Reducible stack.
     * Used during data-flow analysis to compute the maximum stack size.
     * @since 0.6
     */
    @ToString
    private static final class Stack implements InstructionsFlow.Reducible<Stack> {

        /**
         * Stack integer value.
         */
        private final int value;

        /**
         * Where this stack value comes from.
         * This field is needed for debug purposes only.
         * If it creates performance issues, it can be removed.
         */
        private final String source;

        /**
         * Constructor.
         * @param value Stack value.
         */
        Stack(final int value) {
            this(value, "");
        }

        /**
         * Constructor.
         * @param instruction Bytecode instruction.
         */
        Stack(final BytecodeEntry instruction) {
            this(instruction.impact());
        }

        /**
         * Constructor.
         * @param value Stack value.
         * @param source Source of the value.
         */
        private Stack(final int value, final String source) {
            this.value = value;
            this.source = source;
        }

        @Override
        public int compareTo(final Stack other) {
            return Integer.compare(this.value, other.value);
        }

        @Override
        public Stack add(final Stack other) {
            return new Stack(this.value + other.value, other.source);
        }

        @Override
        public Stack enterBlock() {
            return new Stack(1, this.source);
        }

        /**
         * Get integer value.
         * @return Integer value.
         */
        int integer() {
            return this.value;
        }
    }
}
