/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import lombok.ToString;
import org.objectweb.asm.Type;

/**
 * Bytecode method max locals.
 * This class knows hot to compute the maximum number of local variables
 * that can be used by a method.
 * The computation of max locals might give different results if computed only
 * by looking at bytecode instructions, because some of the variables we can see only
 * in the source code are not visible in the bytecode:
 * {@code
 *   public void ia(){
 *     long h$m$$;
 *     boolean U$qa=true,A=U$qa==false,$z;
 *   }
 * }
 * Here, the variable {@code $z} is not visible in the bytecode, because it is not used and it's
 * last variable in the method.
 * @since 0.6
 */
final class MaxLocals {

    /**
     * Method properties.
     */
    private final BytecodeMethodProperties props;

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
     * @param props Method properties.
     * @param instructions Instructions.
     * @param blocks Try-catch blocks.
     */
    MaxLocals(
        final BytecodeMethodProperties props,
        final List<? extends BytecodeEntry> instructions,
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
        return new InstructionsFlow<Variables>(this.instructions, this.blocks)
            .max(
                this.initial(),
                instr -> {
                    final Variables result;
                    if (instr instanceof BytecodeInstruction
                        && ((BytecodeInstruction) instr).isVarInstruction()) {
                        result = new Variables((BytecodeInstruction) instr);
                    } else {
                        result = new Variables();
                    }
                    return result;
                }
            ).orElse(new Variables()).size();
    }

    /**
     * Initial variables.
     * @return Variables.
     */
    private Variables initial() {
        final ArrayList<Integer> init = new ArrayList<>(0);
        if (!this.props.isStatic()) {
            init.add(1);
        }
        Arrays.stream(Type.getArgumentTypes(this.props.descriptor()))
            .map(Type::getSize)
            .forEach(init::add);
        final Map<Integer, Integer> initial = new HashMap<>(0);
        int curr = 0;
        for (final Integer size : init) {
            initial.put(curr, size);
            curr += size;
        }
        return new Variables(initial);
    }

    /**
     * Reducible variables.
     * Used during data-flow analysis to compute the maximum number of local variables.
     * @since 0.6
     */
    @ToString
    private static final class Variables implements InstructionsFlow.Reducible<Variables> {

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
         * @param vars Variables to copy.
         */
        Variables(final Variables vars) {
            this(vars.all);
        }

        /**
         * Constructor.
         * @param instr Bytecode instruction.
         */
        Variables(final BytecodeInstruction instr) {
            this(instr.varIndex(), instr.varSize());
        }

        /**
         * Constructor.
         * @param index Instruction index.
         * @param size Corresponding variable size.
         */
        Variables(final int index, final int size) {
            this(Collections.singletonMap(index, size));
        }

        /**
         * Constructor.
         * @param all All variables.
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

        /**
         * Get size.
         * @return Size.
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
}
