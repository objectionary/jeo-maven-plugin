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
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.objectweb.asm.Label;

/**
 * Data-flow analysis.
 * This class knows how to compute the maximum value of a reducible element based
 * on the instruction flow.
 * @param <T> Type of the reducible element.
 * @since 0.6
 */
public final class InstructionsFlow<T extends InstructionsFlow.Reducible<T>> {

    /**
     * Method instructions.
     */
    private final List<? extends BytecodeEntry> instructions;

    /**
     * Try-catch blocks.
     */
    private final List<BytecodeTryCatchBlock> blocks;

    /**
     * Constructor.
     * @param instr Instructions.
     * @param catches Try-catch blocks.
     */
    InstructionsFlow(
        final List<? extends BytecodeEntry> instr, final List<BytecodeTryCatchBlock> catches
    ) {
        this.instructions = instr;
        this.blocks = new ArrayList<>(catches);
    }

    /**
     * Compute the maximum value for stack or variables.
     * @param initial Initial value.
     * @param generator Function to generate the reducible element from the instruction.
     * @return Maximum value.
     */
    public Optional<T> max(final T initial, final Function<BytecodeEntry, T> generator) {
        final MaxValueMap<Integer, T> visited = new MaxValueMap<>();
        final Deque<Entry<T>> worklist = new ArrayDeque<>(0);
        worklist.push(new Entry<>(0, initial));
        final int total = this.instructions.size();
        T current;
        while (!worklist.isEmpty()) {
            final Entry<T> starting = worklist.pop();
            int index = starting.index();
            current = starting.value();
            if (visited.isGreaterThan(index, current)) {
                continue;
            }
            while (index < total) {
                final BytecodeEntry instruction = this.instructions.get(index);
                final T updated = current.add(generator.apply(instruction));
                if (instruction.isSwitch()) {
                    instruction.jumps().stream().map(this::index)
                        .forEach(ind -> worklist.push(new Entry<>(ind, updated)));
                    visited.putIfGreater(index, updated);
                    break;
                } else if (instruction.isIf()) {
                    final BytecodeLabel label = instruction.jumps().get(0);
                    final int jump = this.index(label);
                    worklist.push(new Entry<>(jump, updated));
                    final int next = index + 1;
                    worklist.push(new Entry<>(next, updated));
                    visited.putIfGreater(index, updated);
                    break;
                } else if (instruction.isJump()) {
                    final BytecodeLabel label = instruction.jumps().get(0);
                    final int jump = this.index(label);
                    worklist.push(new Entry<>(jump, updated));
                    visited.putIfGreater(index, updated);
                    break;
                } else if (instruction.isReturn() || instruction.isThrow()) {
                    visited.putIfGreater(index, updated);
                    break;
                }
                this.suitableBlocks(index)
                    .forEach(ind -> worklist.push(new Entry<>(ind, updated.enterBlock())));
                visited.putIfGreater(index, updated);
                current = updated;
                ++index;
            }
        }
        return visited.values().stream().max(T::compareTo);
    }

    /**
     * Which try-catch-blocks cover the instruction.
     * @param instruction Instruction index.
     * @return List of block indexes.
     */
    private List<Integer> suitableBlocks(final int instruction) {
        return this.blocks.stream()
            .map(BytecodeTryCatchBlock.class::cast)
            .filter(block -> this.index(block.startLabel()) <= instruction)
            .filter(block -> this.index(block.endLabel()) >= instruction)
            .map(block -> this.index(block.handlerLabel()))
            .collect(Collectors.toList());
    }

    /**
     * Index of the label.
     * @param label Label.
     * @return Index.
     */
    private int index(final BytecodeLabel label) {
        for (int index = 0; index < this.instructions.size(); ++index) {
            if (this.instructions.get(index).equals(new BytecodeLabel(label.toString()))) {
                return index;
            }
        }
        throw new IllegalStateException(String.format("Label %s not found", label));
    }

    /**
     * Map with maximum values.
     * @param <K> Key type.
     * @param <V> Value type.
     * @since 0.6
     * @checkstyle IllegalTypeCheck (5 lines)
     */
    private static class MaxValueMap<K, V extends Reducible<V>> extends HashMap<K, V> {

        /**
         * Serial version UID.
         */
        private static final long serialVersionUID = 6517835829882158842L;

        /**
         * Constructor.
         */
        MaxValueMap() {
            super(0);
        }

        /**
         * Is the value greater than the current one?
         * @param key Key.
         * @param value Value.
         * @return True if it is.
         */
        boolean isGreaterThan(final K key, final V value) {
            return this.get(key) != null && this.get(key).compareTo(value) >= 0;
        }

        /**
         * Put the value if it is greater.
         * @param key Key.
         * @param value Value.
         */
        void putIfGreater(final K key, final V value) {
            this.merge(key, value, MaxValueMap::max);
        }

        /**
         * Max of two reducible elements.
         * @param first First element.
         * @param second Second element.
         * @param <T> Type of the element.
         * @return Max element.
         */
        private static <T extends InstructionsFlow.Reducible<T>> T max(
            final T first, final T second
        ) {
            final T result;
            if (first.compareTo(second) > 0) {
                result = first;
            } else {
                result = second;
            }
            return result;
        }
    }

    /**
     * Entry in the worklist.
     * @param <T> Type of the element.
     * @since 0.6
     */
    private static class Entry<T> {
        /**
         * Bytecode instruction index.
         */
        private final int indx;

        /**
         * Value.
         */
        private final T evalue;

        /**
         * Constructor.
         * @param index Index.
         * @param value Value.
         */
        Entry(final int index, final T value) {
            this.indx = index;
            this.evalue = value;
        }

        /**
         * Index.
         * @return Index.
         */
        int index() {
            return this.indx;
        }

        /**
         * Value.
         * @return Value.
         */
        T value() {
            return this.evalue;
        }
    }

    /**
     * Reducible element in the data-flow analysis.
     * @param <T> Type of the element.
     * @since 0.6
     */
    interface Reducible<T> extends Comparable<T> {

        T add(T other);

        T enterBlock();
    }

}
