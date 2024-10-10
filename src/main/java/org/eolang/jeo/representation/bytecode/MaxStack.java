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
import java.util.TreeMap;
import org.objectweb.asm.Label;

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
    MaxStack(
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
    private MaxStack(
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
        int max = 0;
        final Deque<Integer> worklist = new ArrayDeque<>(0);
        final int length = this.instructions.size();
        worklist.add(0);
        Map<Integer, Integer> visited = new TreeMap<>();
        this.blocks.stream()
            .map(BytecodeTryCatchBlock.class::cast)
            .map(BytecodeTryCatchBlock::handler)
            .map(this.instructions::index)
            .peek(ind -> visited.put(ind, 1))
            .forEach(worklist::add);
        while (!worklist.isEmpty()) {
            int current = worklist.pop();
            int stack = visited.get(current) == null ? 0 : visited.get(current);
            while (current < length) {
                BytecodeEntry entry = this.instructions.get(current);
                stack += entry.impact();
                max = Math.max(max, stack);
                final int finalStack = stack;
                visited.compute(
                    current, (k, v) -> v == null ? finalStack : Math.max(v, finalStack)
                );
                if (entry instanceof BytecodeInstruction) {
                    final BytecodeInstruction var = BytecodeInstruction.class.cast(entry);
                    if (var.isSwitch()) {
                        final List<Label> offsets = var.offsets();
                        for (Label offset : offsets) {
                            final int target = this.instructions.index(offset);
                            if (visited.get(target) == null
                                || visited.get(target) < stack
                            ) {
                                visited.put(target, stack);
                                worklist.add(target);
                            }
                        }
                        break;
                    } else if (var.isBranch()) {
                        final Label label = var.jump();
                        final int jump = this.instructions.index(label);
                        if (visited.get(jump) == null
                            || visited.get(jump) < stack
                        ) {
                            visited.put(jump, stack);
                            worklist.add(jump);

                        }
                        final int next = current + 1;
                        if (visited.get(next) == null
                            || visited.get(next) < stack
                        ) {
                            visited.put(next, stack);
                            worklist.add(next);
                        }
                        break;
                    } else if (var.isJump()) {
                        final Label label = var.jump();
                        final int jump = this.instructions.index(label);
                        if (visited.get(jump) == null
                            || visited.get(jump) < stack
                        ) {
                            visited.put(jump, stack);
                            worklist.add(jump);
                        }
                        break;
                    } else if (var.isReturn()) {
                        break;
                    }
                }
                current++;
            }
        }
        return max;
    }
}
