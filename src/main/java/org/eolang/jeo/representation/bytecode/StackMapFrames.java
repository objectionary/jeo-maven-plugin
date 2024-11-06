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
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

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

    /**
     * Method properties.
     */
    private final BytecodeMethodProperties props;

    /**
     * Constructor.
     * @param properties Method properties.
     * @param entries Instructions.
     * @param blocks Try-catch blocks.
     */
    StackMapFrames(
        final BytecodeMethodProperties properties,
        final List<BytecodeEntry> entries,
        final List<BytecodeTryCatchBlock> blocks
    ) {
        this.props = properties;
        this.entries = entries;
        this.blocks = blocks;
    }

    /**
     * Compute the frames.
     * @return The list of frames.
     */
    List<BytecodeFrame> frames() {
        final Deque<Entry> worklist = new ArrayDeque<>(0);
        final Collection<Integer> visited = new HashSet<>(0);
        final Map<Integer, Entry> res = new LinkedHashMap<>(0);
        final int size = this.entries.size();
        final Entry initial = this.initial();
        res.put(0, initial);
        worklist.push(initial);
        while (!worklist.isEmpty()) {
            Entry current = worklist.pop();
            if (visited.contains(current.indx) && current.equals(res.get(current.indx))) {
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
                    res.put(jump, next);
                    visited.add(jump);
                    worklist.push(next);
                } else if (instr.isJump()) {
                    final Label label = instr.jumps().get(0);
                    final int jump = this.index(label);
                    final Entry next = updated.copy(jump);
                    res.put(jump, next);
                    visited.add(jump);
                    worklist.push(next);
                    break;
                } else if (instr.isReturn() || instr.isThrow()) {
                    visited.add(index);
                    break;
                } else {
                    visited.add(index);
                }
                current = updated;
            }
        }
        return this.computeFrames(new ArrayList<>(res.values()));
    }

    private Entry initial() {
        final String descriptor = this.props.descriptor();
        final boolean stat = this.props.isStatic();
        final Type[] types = Type.getArgumentTypes(descriptor);
        int indx = stat ? 0 : 1;
        LinkedHashMap<Integer, Object> locals = new LinkedHashMap<>(0);
        if (!stat) {
            locals.put(0, Opcodes.TOP);
        }
        for (final Type type : types) {
            switch (type.getSort()) {
                case Type.BOOLEAN:
                case Type.BYTE:
                case Type.CHAR:
                case Type.SHORT:
                case Type.INT:
                    locals.put(indx, Opcodes.INTEGER);
                    break;
                case Type.LONG:
                    locals.put(indx, Opcodes.LONG);
                    break;
                case Type.FLOAT:
                    locals.put(indx, Opcodes.FLOAT);
                    break;
                case Type.DOUBLE:
                    locals.put(indx, Opcodes.DOUBLE);
                    break;
                case Type.ARRAY:
                case Type.OBJECT:
                    locals.put(indx, Opcodes.TOP);
                    break;
                default:
                    throw new IllegalStateException(String.format("Type %s not supported", type));
            }
            indx += type.getSize();
        }
        return new Entry(0, locals, new LinkedHashMap<>(0));
    }


    private List<BytecodeFrame> computeFrames(final List<Entry> all) {
        final Entry first = all.remove(0);
        BytecodeFrame previous = new BytecodeFrame(
            Opcodes.F_NEW,
            first.nlocals(),
            first.locals.values().toArray(),
            first.nstack(),
            first.stack.values().toArray()
        );
        final List<BytecodeFrame> res = new ArrayList<>(all.size());
        for (final Entry entry : all) {
            final BytecodeFrame difference = this.difference(previous, entry);
            res.add(difference);
            previous = difference;
        }
        return res;
    }

    private BytecodeFrame difference(final BytecodeFrame previous, final Entry entry) {
        final BytecodeFrame next = entry.toFrame();
        if (previous.sameLocals(next) && next.stackEmpty()) {
            return next.substract(previous).withType(Opcodes.F_SAME);
        } else if (previous.sameLocals(next) && next.stackOneElement()) {
            return next.substract(previous).withType(Opcodes.F_SAME1);
        } else {
            final int diff = next.localsDiff(previous);
            if (diff < 0) {
                return next.substract(previous).withType(Opcodes.F_CHOP);
            } else {
                return next.substract(previous).withType(Opcodes.F_APPEND);
            }
        }
//        return next.withType(Opcodes.F_FULL);
    }

    /**
     * Entry in the worklist.
     * @since 0.6
     */
    @ToString
    @EqualsAndHashCode
    private static class Entry {
        /**
         * Bytecode instruction index.
         */
        private final int indx;

        private final Map<Integer, Object> locals;

        private final Map<Integer, Object> stack;

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
                if (copy.containsKey(instruction.localIndex())) {
                    final Object current = copy.get(instruction.localIndex());
                    final Object instr = instruction.elementType();
                    if (!current.equals(instr)) {
                        copy.put(instruction.localIndex(), Opcodes.TOP);
                    }
                } else {
                    copy.put(instruction.localIndex(), instruction.elementType());
                }
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

}
