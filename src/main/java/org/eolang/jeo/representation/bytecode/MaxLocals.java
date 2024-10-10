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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.ToString;
import org.objectweb.asm.Label;
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
    MaxLocals(
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
     * @param catches Try-catch blocks.
     */
    private MaxLocals(
        final BytecodeMethodProperties props,
        final InstructionsFlow instructions,
        final List<BytecodeTryCatchBlock> catches
    ) {
        this.props = props;
        this.instructions = instructions;
        this.blocks = catches;
    }

    /**
     * Compute the maximum number of local variables.
     * @return Maximum number of local variables.
     */
    public int value() {
        Variables initial = new Variables();
        int first = 0;
        if (!this.props.isStatic()) {
            initial.put(0, 1);
            first = 1;
        }
        final Type[] types = Type.getArgumentTypes(this.props.descriptor());
        for (int index = 0; index < types.length; index++) {
            final Type type = types[index];
            initial.put(index * type.getSize() + first, type.getSize());
        }
        final Map<Integer, Variables> all = new TreeMap<>();
        final Map<Integer, Variables> worklist = new HashMap<>(0);
        worklist.put(0, initial);
        final int total = this.instructions.size();
        Variables currentVars;
        while (!worklist.isEmpty()) {
            final Map.Entry<Integer, Variables> curr = worklist.entrySet()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(""));
            int current = curr.getKey();
            worklist.remove(current);
            if (all.get(current) != null) {
                continue;
            }
            currentVars = new Variables(curr.getValue());
            while (current < total) {
                BytecodeEntry entry = this.instructions.get(current);
                if (entry instanceof BytecodeInstruction) {
                    final BytecodeInstruction var = BytecodeInstruction.class.cast(entry);
                    if (var.isSwitch()) {
                        final List<Label> offsets = var.offsets();
                        for (Label offset : offsets) {
                            final int target = this.instructions.index(offset);
                            worklist.put(target, new Variables(currentVars));
                        }
                        break;
                    } else if (var.isBranch()) {
                        final Label label = var.jump();
                        final int jump = this.instructions.index(label);
                        worklist.put(jump, new Variables(currentVars));
                        final int next = current + 1;
                        worklist.put(next, new Variables(currentVars));
                        break;
                    } else if (var.isJump()) {
                        final Label label = var.jump();
                        final int jump = this.instructions.index(label);
                        worklist.put(jump, new Variables(currentVars));
                        break;
                    } else if (var.isReturn()) {
                        break;
                    } else if (var.isVarInstruction()) {
                        currentVars.put(var);
                    }
                }
                final Variables value = new Variables(currentVars);
                this.suitableBlocks(current).stream().forEach(ind -> {
                    worklist.put(ind, new Variables(value));
                });
                all.put(current, value);
                current++;
            }
        }
        return all.values().stream().mapToInt(Variables::size).max().orElse(0);
    }

    /**
     * Which try-catch-blocks cover the instruction.
     * @param instruction Instruction index.
     * @return List of block indexes.
     */
    private List<Integer> suitableBlocks(final int instruction) {
        return this.blocks.stream()
            .map(BytecodeTryCatchBlock.class::cast)
            .filter(block -> this.instructions.index(block.start()) <= instruction)
            .filter(block -> this.instructions.index(block.end()) >= instruction)
            .map(block -> this.instructions.index(block.handler()))
            .collect(Collectors.toList());
    }

    /**
     * Variables.
     * @since 0.6
     */
    @ToString
    private static class Variables {

        /**
         * All variables.
         */
        private final TreeMap<Integer, Integer> all;

        /**
         * Constructor.
         */
        Variables() {
            this(new HashMap<>(0));
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
         * @param all All variables.
         */
        private Variables(final Map<Integer, Integer> all) {
            this.all = new TreeMap<>(all);
        }

        /**
         * Get size.
         * @return Size.
         */
        int size() {
            int result = 0;
            if (!this.all.isEmpty()) {
                final Map.Entry<Integer, Integer> entry = this.all.lastEntry();
                result = (entry.getKey() + 1) + ((int) (entry.getValue() * 0.5));
            }
            return result;
        }

        /**
         * Put variable.
         * @param var
         */
        void put(final BytecodeInstruction var) {
            this.put(var.varIndex(), var.varSize());
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
