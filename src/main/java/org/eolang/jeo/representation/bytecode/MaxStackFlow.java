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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import lombok.ToString;
import org.objectweb.asm.Label;

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
    @SuppressWarnings("PMD.CognitiveComplexity")
    public int value() {
        return new DataFlow<>(
            this.instructions,
            this.blocks,
            new Stack(0),
            Stack::new
        ).max().value;

//        int max = 0;
//        final int length = this.instructions.size();
//        final Deque<Integer> worklist = new ArrayDeque<>(0);
//        worklist.add(0);
//        final Map<Integer, Integer> visited = new TreeMap<>();
//        this.blocks.stream()
//            .map(BytecodeTryCatchBlock.class::cast)
//            .map(BytecodeTryCatchBlock::handlerLabel)
//            .map(this.instructions::index)
//            .peek(ind -> visited.put(ind, 1))
//            .forEach(worklist::add);
//        while (!worklist.isEmpty()) {
//            int current = worklist.pop();
//            int stack = visited.get(current) == null ? 0 : visited.get(current);
//            while (current < length) {
//                final BytecodeEntry entry = this.instructions.get(current);
//                stack += entry.impact();
//                max = Math.max(max, stack);
//                final int fstack = stack;
//                visited.compute(
//                    current, (k, v) -> v == null ? fstack : Math.max(v, fstack)
//                );
//                if (entry instanceof BytecodeInstruction) {
//                    final BytecodeInstruction var = BytecodeInstruction.class.cast(entry);
//                    if (var.isSwitch()) {
//                        final List<Label> offsets = var.offsets();
//                        for (final Label offset : offsets) {
//                            final int target = this.instructions.index(offset);
//                            if (visited.get(target) == null
//                                || visited.get(target) < stack
//                            ) {
//                                visited.put(target, stack);
//                                worklist.add(target);
//                            }
//                        }
//                        break;
//                    } else if (var.isBranch()) {
//                        final Label label = var.jump();
//                        final int jump = this.instructions.index(label);
//                        if (visited.get(jump) == null
//                            || visited.get(jump) < stack
//                        ) {
//                            visited.put(jump, stack);
//                            worklist.add(jump);
//                        }
//                        final int next = current + 1;
//                        if (visited.get(next) == null
//                            || visited.get(next) < stack
//                        ) {
//                            visited.put(next, stack);
//                            worklist.add(next);
//                        }
//                        break;
//                    } else if (var.isJump()) {
//                        final Label label = var.jump();
//                        final int jump = this.instructions.index(label);
//                        if (visited.get(jump) == null
//                            || visited.get(jump) < stack
//                        ) {
//                            visited.put(jump, stack);
//                            worklist.add(jump);
//                        }
//                        break;
//                    } else if (var.isReturn()) {
//                        break;
//                    }
//                }
//                ++current;
//            }
//        }
//        return max;
    }

    @ToString
    private static class Stack implements DataFlow.Something<Stack> {

        private final int value;
        private final String source;

        private Stack(final int value) {
            this(value, "");
        }

        private Stack(final BytecodeInstruction instruction) {
            this(instruction.impact(), instruction.testCode());
        }

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
        public Stack initBlock() {
//            return this.add(new Stack(1));
            return new Stack(1);
        }
    }
}
