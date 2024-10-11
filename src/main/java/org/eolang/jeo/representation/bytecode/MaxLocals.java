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
                    if (instr.isVarInstruction()) {
                        return new Variables(instr);
                    } else {
                        return new Variables();
                    }
                }
            ).size();
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
        Map<Integer, Integer> initial = new HashMap<>(0);
        int curr = 0;
        while (curr < init.size()) {
            final Integer size = init.get(curr);
            initial.put(curr, size);
            curr += size;
        }
        return new Variables(initial);
    }

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
        Variables(Variables vars) {
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
        public int compareTo(final Variables o) {
            return Integer.compare(this.size(), o.size());
        }

        @Override
        public Variables add(final Variables other) {
            Map<Integer, Integer> variables = new HashMap<>();
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
