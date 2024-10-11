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

public final class MaxStackFlow {

    /**
     * Method instructions.
     */
    private final InstructionsFlow instructions;

    /**
     * Try-catch blocks.
     */
    private final List<BytecodeTryCatchBlock> blocks;


    /**
     * Constructor.
     * @param instructions Instructions.
     * @param catches Try-catch blocks.
     */
    MaxStackFlow(
        final List<? extends BytecodeEntry> instructions,
        final List<BytecodeTryCatchBlock> catches
    ) {
        this(new InstructionsFlow(instructions), catches);
    }

    /**
     * Compute the maximum stack size.
     * @param instructions Instructions.
     * @param catches Try-catch blocks.
     */
    private MaxStackFlow(
        final InstructionsFlow instructions,
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
        return new DataFlow<Stack>(this.instructions, this.blocks)
            .max(new Stack(0), Stack::new)
            .value();
    }

    @ToString
    private static class Stack implements DataFlow.Reducible<Stack> {

        private final int value;
        private final String source;

        Stack(final int value) {
            this(value, "");
        }

        Stack(final BytecodeInstruction instruction) {
            this(instruction.impact(), instruction.testCode());
        }

        private Stack(final int value, final String source) {
            this.value = value;
            this.source = source;
        }

        int value() {
            return this.value;
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
            return new Stack(1);
        }
    }
}
