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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.objectweb.asm.Label;

public final class DataFlow<T extends DataFlow.Reducible<T>> {
    private final InstructionsFlow instructions;
    private final List<BytecodeTryCatchBlock> blocks;

    DataFlow(final InstructionsFlow instr, final List<BytecodeTryCatchBlock> catches) {
        this.instructions = instr;
        this.blocks = new ArrayList<>(catches);
    }

    public T max(T initial, Function<BytecodeInstruction, T> generator) {
        final Map<Integer, T> visited = new HashMap<>(0);
        final Map<Integer, T> worklist = new HashMap<>(0);
        worklist.put(0, initial);
        final int total = this.instructions.size();
        T current;
        while (!worklist.isEmpty()) {
            final Map.Entry<Integer, T> curr = worklist.entrySet()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find first worklist element"));
            int index = curr.getKey();
            current = curr.getValue();
            worklist.remove(index);
            if (visited.get(index) != null) {
                if (visited.get(index).compareTo(current) >= 0) {
                    continue;
                }
            }
            while (index < total) {
                final BytecodeEntry entry = this.instructions.get(index);
                if (entry instanceof BytecodeInstruction) {
                    final BytecodeInstruction instruction = BytecodeInstruction.class.cast(entry);
                    current = current.add(generator.apply(instruction));
                    final T updated = current;
                    if (instruction.isSwitch()) {
                        final List<Label> offsets = instruction.offsets();
                        for (final Label offset : offsets) {
                            final int target = this.instructions.index(offset);
                            worklist.put(target, updated);
                        }
                        visited.put(index, updated);
                        break;
                    } else if (instruction.isBranch()) {
                        final Label label = instruction.jump();
                        final int jump = this.instructions.index(label);
                        worklist.put(jump, updated);
                        final int next = index + 1;
                        worklist.put(next, updated);
                        visited.put(index, updated);
                        break;
                    } else if (instruction.isJump()) {
                        final Label label = instruction.jump();
                        final int jump = this.instructions.index(label);
                        worklist.put(jump, updated);
                        visited.put(index, updated);
                        break;
                    } else if (instruction.isReturn()) {
                        visited.put(index, updated);
                        break;
                    }
                    this.suitableBlocks(index)
                        .forEach(ind -> worklist.put(ind, updated.enterBlock()));
                    visited.putIfAbsent(index, updated);
                    visited.computeIfPresent(index, (k, v) -> this.max(v, updated));
                } else {
                    visited.put(index, current);
                }
                ++index;
            }
        }
        return visited.values()
            .stream()
            .max(T::compareTo)
            .orElseThrow(() -> new IllegalStateException("Cannot find max value"));
    }

    /**
     * Which try-catch-blocks cover the instruction.
     * @param instruction Instruction index.
     * @return List of block indexes.
     */
    private List<Integer> suitableBlocks(final int instruction) {
        return this.blocks.stream()
            .map(BytecodeTryCatchBlock.class::cast)
            .filter(block -> this.instructions.index(block.startLabel()) <= instruction)
            .filter(block -> this.instructions.index(block.endLabel()) >= instruction)
            .map(block -> this.instructions.index(block.handlerLabel()))
            .collect(Collectors.toList());
    }

    private T max(final T first, final T second) {
        return first.compareTo(second) > 0 ? first : second;
    }

    interface Reducible<T> extends Comparable<T> {

        T add(T other);

        T enterBlock();

    }
}
