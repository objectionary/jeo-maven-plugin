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

import com.jcabi.log.Logger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.Label;

/**
 * Bytecode method max stack.
 * This class knows hot to compute the maximum size of the stack
 * that can be used by a method.
 * @since 0.6
 */
public final class MaxStack {

    private final BytecodeMethodProperties properties;
    private final List<BytecodeEntry> instructions;
    private final List<BytecodeTryCatchBlock> tryblocks;

    public MaxStack(
        final BytecodeMethodProperties properties, final List<BytecodeEntry> instructions,
        final List<BytecodeTryCatchBlock> tryblocks
    ) {
        this.properties = properties;
        this.instructions = instructions;
        this.tryblocks = tryblocks;
    }

    public int value() {
        Logger.info(this, "Computing stack for %s", this.properties);
        int max = 0;
        final Deque<Integer> worklist = new ArrayDeque<>(0);
        final int length = this.instructions.size();
        worklist.add(0);
        Map<Integer, Integer> visited = new TreeMap<>();
        this.tryblocks.stream()
            .map(BytecodeTryCatchBlock.class::cast)
            .map(BytecodeTryCatchBlock::handler)
            .map(this::index)
            .peek(ind -> visited.put(ind, 1))
            .forEach(worklist::add);
        while (!worklist.isEmpty()) {
            int current = worklist.pop();
            int stack = visited.get(current) == null ? 0 : visited.get(current);
            while (current < length) {
                BytecodeEntry entry = this.instructions.get(current);
                stack += entry.stackImpact();
                max = Math.max(max, stack);
                final int finalStack = stack;
                visited.compute(
                    current, (k, v) -> v == null ? finalStack : Math.max(v, finalStack)
                );
                if (entry instanceof BytecodeInstruction) {
                    final BytecodeInstruction var = BytecodeInstruction.class.cast(entry);
                    if (var.isBranchInstruction()) {
                        if (var.isSwitchInstruction()) {
                            final List<Label> offsets = var.offsets();
                            for (Label offset : offsets) {
                                final int target = this.index(offset);
                                if (visited.get(target) == null
                                    || visited.get(target) < stack
                                ) {
                                    visited.put(target, stack);
                                    worklist.add(target);
                                }
                            }
                            break;
                        } else if (var.isConditionalBranchInstruction()) {
                            final int jump = this.index(var.offset());
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
                        } else if (var.isReturnInstruction()) {
                            break;
                        } else {
                            final int jump = this.index(var.offset());
                            if (visited.get(jump) == null
                                || visited.get(jump) < stack
                            ) {
                                visited.put(jump, stack);
                                worklist.add(jump);
                            }
                            break;
                        }
                    }
                }
                current++;
            }
        }
        return max;
    }


    private int index(final Label label) {
        for (int index = 0; index < this.instructions.size(); index++) {
            final BytecodeEntry entry = this.instructions.get(index);
            final BytecodeLabel obj = new BytecodeLabel(label, new AllLabels());
            final boolean equals = entry.equals(obj);
            if (equals) {
                return index;
            }
        }
        throw new IllegalStateException("Label not found");
    }
}
