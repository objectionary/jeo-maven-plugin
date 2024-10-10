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

    private final BytecodeMethodProperties props;
    private final InstructionsFlow instructions;
    private final List<BytecodeTryCatchBlock> blocks;

    MaxLocals(
        final BytecodeMethodProperties props,
        final List<BytecodeEntry> instructions,
        final List<BytecodeTryCatchBlock> catches
    ) {
        this(props, new InstructionsFlow(instructions), catches);
    }

    private MaxLocals(
        final BytecodeMethodProperties props,
        final InstructionsFlow instructions,
        final List<BytecodeTryCatchBlock> catches
    ) {
        this.props = props;
        this.instructions = instructions;
        this.blocks = catches;
    }

    public int value() {
        Logger.info(this, "Computing locals for %s", this.props);
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
        Map<Integer, Variables> all = new TreeMap<>();
        Map<Integer, Variables> worklist = new HashMap<>();
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
                    if (var.isBranchInstruction()) {
                        if (var.isSwitchInstruction()) {
                            final List<Label> offsets = var.offsets();
                            for (Label offset : offsets) {
                                final int target = this.instructions.index(offset);
                                worklist.put(target, new Variables(currentVars));
                            }
                            break;
                        } else if (var.isConditionalBranchInstruction()) {
                            final Label label = var.offset();
                            final int jump = this.instructions.index(label);
                            worklist.put(jump, new Variables(currentVars));
                            final int next = current + 1;
                            worklist.put(next, new Variables(currentVars));
                            break;
                        } else {
                            final Label label = var.offset();
                            final int jump = this.instructions.index(label);
                            worklist.put(jump, new Variables(currentVars));
                            break;
                        }
                    } else if (var.isReturnInstruction()) {
                        break;
                    }
                    if (var.isVarInstruction()) {
                        currentVars.put(var);
                    }
                }
                final Variables value = new Variables(currentVars);
                this.catches(current).stream().forEach(ind -> {
                    worklist.put(ind, new Variables(value));
                });
                all.put(current, value);
                current++;
            }
        }
        return all.values().stream().mapToInt(Variables::size).max().orElse(0);
    }

    private List<Integer> catches(final int instruction) {
        return this.blocks.stream().map(BytecodeTryCatchBlock.class::cast)
            .filter(
                block -> {
                    if (this.instructions.index(block.start()) > instruction) return false;
                    return this.instructions.index(block.end()) >= instruction;
                }
            ).map(
                block -> {
                    return this.instructions.index(block.handler());
                }
            ).collect(Collectors.toList());
    }

    @ToString
    private static class Variables {

        private final TreeMap<Integer, Integer> all;

        public Variables() {
            this(new HashMap<>(0));
        }

        public Variables(final Variables vars) {
            this(vars.all);
        }

        public Variables(final Map<Integer, Integer> all) {
            this.all = new TreeMap<>(all);
        }

        int size() {
            if (all.isEmpty()) {
                return 0;
            }
            final Map.Entry<Integer, Integer> entry = all.lastEntry();
            return (entry.getKey() + 1) + ((int) (entry.getValue() * 0.5));
        }

        void put(BytecodeInstruction var) {
            this.put(var.localIndex(), var.size());
        }

        void put(int index, int value) {
            this.all.put(index, value);
        }
    }
}
