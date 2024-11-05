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
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

/**
 * This class knows how to compute the stack map frames from the bytecode entries.
 * @since 0.6
 */
final class StackMapFrames {

    /**
     * The instructions.
     */
    private final List<BytecodeEntry> entries;

    /**
     * The try-catch blocks.
     */
    private final List<BytecodeTryCatchBlock> blocks;

    StackMapFrames(
        final List<BytecodeEntry> entries,
        final List<BytecodeTryCatchBlock> blocks
    ) {
        this.entries = entries;
        this.blocks = blocks;
    }

    /**
     * Compute the frames.
     * @return The list of frames.
     */
    List<BytecodeFrame> frames() {
        Deque<Entry> worklist = new ArrayDeque<>(0);
        // todo: parameters? static?
        Entry initial = new Entry(0);
        worklist.push(initial);
        final int size = this.entries.size();
        List<BytecodeFrame> res = new ArrayList<>(0);
        Set<Integer> visited = new HashSet<>(0);

        while (!worklist.isEmpty()) {
            Entry current = worklist.pop();
            if (visited.contains(current.indx)) {
                continue;
            }
            for (int index = current.indx(); index < size; ++index) {
                final BytecodeEntry entry = this.entries.get(index);
                if (!(entry instanceof BytecodeInstruction)) {
                    continue;
                }
                final BytecodeInstruction instr = (BytecodeInstruction) entry;
                final Entry updated = current.append(index, instr);
                if (instr.isIf()) {
                    final Label label = instr.jumps().get(0);
                    final int jump = this.index(label);
                    final Entry next = updated.copy(jump);
                    res.add(next.toFrame());
                    visited.add(jump);
                    worklist.push(next);
                } else if (instr.isJump()) {
                    final Label label = instr.jumps().get(0);
                    final int jump = this.index(label);
                    final Entry next = updated.copy(jump);
                    res.add(next.toFrame());
                    visited.add(jump);
                    worklist.push(next);
                    break;
                } else if (instr.isReturn() || instr.isThrow()) {
                    break;
                }
                current = updated;
            }
        }
        return res;
    }

    /**
     * Index of the label.
     * @param label Label.
     * @return Index.
     */
    private int index(final Label label) {
        for (int index = 0; index < this.entries.size(); ++index) {
            if (this.entries.get(index).equals(new BytecodeLabel(label))) {
                return index;
            }
        }
        throw new IllegalStateException(String.format("Label %s not found", label));
    }


    /**
     * Entry in the worklist.
     * @since 0.6
     */
    private static class Entry {
        /**
         * Bytecode instruction index.
         */
        private final int indx;

        private final Map<Integer, Object> locals;

        private final Map<Integer, Object> stack;

        public Entry(final int indx) {
            this(indx, new LinkedHashMap<>(0), new LinkedHashMap<>(0));
        }

        private Entry(
            final int indx, final Map<Integer, Object> locals, final Map<Integer, Object> stack
        ) {
            this.indx = indx;
            this.locals = locals;
            this.stack = stack;
        }

        Entry copy(final int indx) {
            return new Entry(
                indx,
                this.locals,
                this.stack
            );
        }

        int indx() {
            return this.indx;
        }

        int nlocals() {
            return this.locals.size();
        }

        int nstack() {
            return this.stack.size();
        }

        Entry append(final int indx, final BytecodeInstruction instruction) {
            if (instruction.isVarInstruction()) {
                final LinkedHashMap<Integer, Object> copy = new LinkedHashMap<>(this.locals);
                copy.put(instruction.localIndex(), instruction.localType());
                return new Entry(
                    indx,
                    copy,
                    this.stack
                );
            } else {
                return new Entry(
                    indx,
                    this.locals,
                    this.stack
                );
            }
        }

        BytecodeFrame toFrame() {
            return new BytecodeFrame(
                Opcodes.F_NEW,
                this.nlocals(),
                this.locals.values().toArray(),
                this.nstack(),
                this.stack.values().toArray()
            );
        }
    }
}
