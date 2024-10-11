/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
         * Stack integer value;
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
        Stack(final BytecodeInstruction instruction) {
            this(instruction.impact(), instruction.testCode());
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
        public int compareTo(final Stack o) {
            return Integer.compare(this.value, o.value);
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
