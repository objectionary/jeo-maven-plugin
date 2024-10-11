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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.ToString;
import org.objectweb.asm.Type;

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
        final Variables initial = new Variables();
        int first = 0;
        if (!this.props.isStatic()) {
            initial.put(0, 1);
            first = 1;
        }
        final Type[] types = Type.getArgumentTypes(this.props.descriptor());
        for (int index = 0; index < types.length; ++index) {
            final Type type = types[index];
            initial.put(index * type.getSize() + first, type.getSize());
        }
        return new DataFlow<Variables>(this.instructions, this.blocks)
            .max(initial, instr -> {
                if (instr.isVarInstruction()) {
                    return new Variables(instr);
                } else {
                    return new Variables();
                }
            })
            .size();
    }

    @ToString
    private static final class Variables implements DataFlow.Reducible<Variables> {

        /**
         * All variables with their sizes.
         */
        private final TreeMap<Integer, Integer> all;

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

        Variables(final BytecodeInstruction instr) {
            this(instr.varIndex(), instr.varSize());
        }

        Variables(final int index, final int size) {
            this(Collections.singletonMap(index, size));
        }

        /**
         * Constructor.
         * @param all All variables.
         */
        private Variables(final Map<Integer, Integer> all) {
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
            int result = 0;
            if (!this.all.isEmpty()) {
                final Map.Entry<Integer, Integer> entry = this.all.lastEntry();
                result = entry.getKey() + 1 + (int) (entry.getValue() * 0.5);
            }
            return result;
        }

        /**
         * Put variable.
         * @param index Index.
         * @param value Value.
         */
        void put(final int index, final int value) {
            this.all.put(index, value);
        }
    }
}
