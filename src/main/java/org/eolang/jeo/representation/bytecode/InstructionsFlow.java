/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Data-flow analysis.
 * This class knows how to compute the maximum value of a reducible element based
 * on the instruction flow.
 *
 * @param <T> Type of the reducible element
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
     *
     * @param instr Instructions
     * @param catches Try-catch blocks
     */
    InstructionsFlow(
        final List<? extends BytecodeEntry> instr, final List<BytecodeTryCatchBlock> catches
    ) {
        this.instructions = instr;
        this.blocks = new ArrayList<>(catches);
    }

    /**
     * Compute the maximum value for stack or variables.
     *
     * @param initial Initial value
     * @param generator Function to generate the reducible element from the instruction
     * @return Maximum value
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
                this.suitableBlocks(index).forEach(
                    ind -> worklist.push(new Entry<>(ind, updated.enterBlock()))
                );
                visited.putIfGreater(index, updated);
                current = updated;
                ++index;
            }
        }
        return visited.values().stream().max((first, second) -> first.compareTo(second));
    }

    private List<Integer> suitableBlocks(final int instruction) {
        return this.blocks.stream()
            .map(BytecodeTryCatchBlock.class::cast)
            .filter(block -> this.index(block.startLabel()) <= instruction)
            .filter(block -> this.index(block.endLabel()) >= instruction)
            .map(block -> this.index(block.handlerLabel()))
            .collect(Collectors.toList());
    }

    private int index(final BytecodeLabel label) {
        for (int index = 0; index < this.instructions.size(); ++index) {
            if (this.instructions.get(index).equals(label)) {
                return index;
            }
        }
        throw new IllegalStateException(String.format("Label %s not found", label));
    }

    /**
     * Reducible element in the data-flow analysis.
     * Self-bounded by design: implementers declare {@code T extends Reducible<T>}, so comparing
     * to {@code T} is intentional and safe, even though the checker cannot see that bound here.
     *
     * @param <T> Type of the element
     * @since 0.6
     */
    @SuppressWarnings("ComparableType")
    interface Reducible<T> extends Comparable<T> {

        T add(T other);

        T enterBlock();
    }
}
