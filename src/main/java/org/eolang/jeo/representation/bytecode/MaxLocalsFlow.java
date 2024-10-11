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

public final class MaxLocalsFlow {

    /**
     * Method properties.
     */
    private final BytecodeMethodProperties props;

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
     * @param props Method properties.
     * @param instructions Instructions.
     * @param catches Try-catch blocks.
     */
    MaxLocalsFlow(
        final BytecodeMethodProperties props,
        final List<? extends BytecodeEntry> instructions,
        final List<BytecodeTryCatchBlock> catches
    ) {
        this(props, new InstructionsFlow(instructions), catches);
    }

    /**
     * Constructor.
     * @param props Method properties.
     * @param instructions Instructions.
     * @param blocks Try-catch blocks.
     */
    private MaxLocalsFlow(
        final BytecodeMethodProperties props,
        final InstructionsFlow instructions,
        final List<BytecodeTryCatchBlock> blocks
    ) {
        this.props = props;
        this.instructions = instructions;
        this.blocks = blocks;
    }

    /**
     * Compute the maximum number of local variables.
     * @return Maximum number of local variables.
     */
    public int value() {
        return new DataFlow<Variables>(this.instructions, this.blocks)
            .max(new Variables(), instr -> new Variables())
            .size();
    }

    @ToString
    private static final class Variables implements DataFlow.Reducible<Variables> {

        @Override
        public int compareTo(final Variables o) {
            return 0;
        }

        @Override
        public Variables add(final Variables other) {
            return null;
        }

        @Override
        public Variables enterBlock() {
            return this;
        }

        int size() {
            return 0;
        }
    }
}
