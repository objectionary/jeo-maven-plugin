/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Reducible variables.
 * Used during data-flow analysis to compute the maximum number of local variables.
 *
 * @since 0.6
 */
final class Variables implements InstructionsFlow.Reducible<Variables> {

    /**
     * All variables with their sizes.
     */
    private final NavigableMap<Integer, Integer> all;

    /**
     * Constructor.
     */
    Variables() {
        this(new TreeMap<>());
    }

    /**
     * Copy constructor.
     *
     * @param vars Variables to copy
     */
    Variables(final Variables vars) {
        this(vars.all);
    }

    /**
     * Constructor.
     *
     * @param instr Bytecode instruction
     */
    Variables(final BytecodeInstruction instr) {
        this(instr.varIndex(), instr.varSize());
    }

    /**
     * Constructor.
     *
     * @param index Instruction index
     * @param size Corresponding variable size
     */
    Variables(final int index, final int size) {
        this(Collections.singletonMap(index, size));
    }

    /**
     * Constructor.
     *
     * @param all All variables
     */
    Variables(final Map<Integer, Integer> all) {
        this.all = new TreeMap<>(all);
    }

    @Override
    public int compareTo(final Variables other) {
        return Integer.compare(this.size(), other.size());
    }

    @Override
    public Variables add(final Variables other) {
        final Map<Integer, Integer> variables = new HashMap<>();
        variables.putAll(this.all);
        variables.putAll(other.all);
        return new Variables(variables);
    }

    @Override
    public Variables enterBlock() {
        return new Variables(this);
    }

    @Override
    public String toString() {
        return String.format("Variables(all=%s)", this.all);
    }

    /**
     * Get size.
     *
     * @return Size
     */
    int size() {
        final int result;
        if (this.all.isEmpty()) {
            result = 0;
        } else {
            final Map.Entry<Integer, Integer> biggest = this.all.lastEntry();
            result = biggest.getKey() + 1 + (int) (biggest.getValue() * 0.5);
        }
        return result;
    }
}
