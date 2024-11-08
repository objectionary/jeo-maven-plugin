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
package org.eolang.jeo.representation.asm;

import java.util.Arrays;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeEntry;
import org.eolang.jeo.representation.bytecode.BytecodeFrame;
import org.eolang.jeo.representation.bytecode.BytecodeInstruction;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeLine;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Asm instruction.
 * @since 0.6
 */
final class AsmInstruction {

    /**
     * Instruction node.
     */
    private final AbstractInsnNode node;

    /**
     * Constructor.
     * @param node Instruction node.
     */
    AsmInstruction(final AbstractInsnNode node) {
        this.node = node;
    }

    /**
     * Convert asm instruction to domain instruction.
     * @return Domain instruction.
     * @checkstyle CyclomaticComplexityCheck (100 lines)
     * @checkstyle JavaNCSSCheck (100 lines)
     */
    @SuppressWarnings({"PMD.NcssCount", "PMD.ExcessiveMethodLength"})
    BytecodeEntry bytecode() {
        final BytecodeEntry result;
        switch (this.node.getType()) {
            case AbstractInsnNode.INSN:
                result = new BytecodeInstruction(this.node.getOpcode());
                break;
            case AbstractInsnNode.INT_INSN:
                final IntInsnNode instr = IntInsnNode.class.cast(this.node);
                result = new BytecodeInstruction(
                    instr.getOpcode(),
                    instr.operand
                );
                break;
            case AbstractInsnNode.VAR_INSN:
                final VarInsnNode varinstr = VarInsnNode.class.cast(this.node);
                result = new BytecodeInstruction(
                    varinstr.getOpcode(), varinstr.var
                );
                break;
            case AbstractInsnNode.TYPE_INSN:
                final TypeInsnNode typeinstr = TypeInsnNode.class.cast(this.node);
                result = new BytecodeInstruction(
                    typeinstr.getOpcode(),
                    typeinstr.desc
                );
                break;
            case AbstractInsnNode.FIELD_INSN:
                final FieldInsnNode field = FieldInsnNode.class.cast(this.node);
                result = new BytecodeInstruction(
                    field.getOpcode(),
                    field.owner,
                    field.name,
                    field.desc
                );
                break;
            case AbstractInsnNode.METHOD_INSN:
                final MethodInsnNode method = MethodInsnNode.class.cast(this.node);
                result = new BytecodeInstruction(
                    method.getOpcode(),
                    method.owner,
                    method.name,
                    method.desc,
                    method.itf
                );
                break;
            case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
                final InvokeDynamicInsnNode dynamic = InvokeDynamicInsnNode.class.cast(this.node);
                result = new BytecodeInstruction(
                    dynamic.getOpcode(),
                    Stream.concat(
                        Stream.of(
                            dynamic.name,
                            dynamic.desc,
                            dynamic.bsm
                        ),
                        Arrays.stream(dynamic.bsmArgs)
                    ).toArray(Object[]::new)
                );
                break;
            case AbstractInsnNode.JUMP_INSN:
                final JumpInsnNode jump = JumpInsnNode.class.cast(this.node);
                result = new BytecodeInstruction(
                    jump.getOpcode(),
                    jump.label.getLabel()
                );
                break;
            case AbstractInsnNode.LABEL:
                final LabelNode label = LabelNode.class.cast(this.node);
                result = new BytecodeLabel(
                    label.getLabel()
                );
                break;
            case AbstractInsnNode.LDC_INSN:
                final LdcInsnNode ldc = LdcInsnNode.class.cast(this.node);
                result = new BytecodeInstruction(
                    ldc.getOpcode(),
                    ldc.cst
                );
                break;
            case AbstractInsnNode.IINC_INSN:
                final IincInsnNode iinc = IincInsnNode.class.cast(this.node);
                result = new BytecodeInstruction(
                    iinc.getOpcode(),
                    iinc.var,
                    iinc.incr
                );
                break;
            case AbstractInsnNode.TABLESWITCH_INSN:
                final TableSwitchInsnNode table = TableSwitchInsnNode.class.cast(this.node);
                result = new BytecodeInstruction(
                    table.getOpcode(),
                    Stream.concat(
                        Stream.of(table.min, table.max, table.dflt.getLabel()),
                        table.labels.stream().map(LabelNode::getLabel)
                    ).toArray(Object[]::new)
                );
                break;
            case AbstractInsnNode.LOOKUPSWITCH_INSN:
                final LookupSwitchInsnNode lookup = LookupSwitchInsnNode.class.cast(this.node);
                result = new BytecodeInstruction(
                    lookup.getOpcode(),
                    Stream.concat(
                        Stream.of(lookup.dflt.getLabel()),
                        Stream.concat(
                            lookup.keys.stream(),
                            lookup.labels.stream().map(LabelNode::getLabel)
                        )
                    ).toArray(Object[]::new)
                );
                break;
            case AbstractInsnNode.MULTIANEWARRAY_INSN:
                final MultiANewArrayInsnNode multiarr = MultiANewArrayInsnNode.class.cast(
                    this.node
                );
                result = new BytecodeInstruction(
                    multiarr.getOpcode(),
                    multiarr.desc,
                    multiarr.dims
                );
                break;
            case AbstractInsnNode.FRAME:
                final FrameNode frame = FrameNode.class.cast(this.node);
                result = new BytecodeFrame(frame.type, frame.local, frame.stack);
                break;
            case AbstractInsnNode.LINE:
                result = new BytecodeLine();
                break;
            default:
                throw new IllegalStateException(
                    String.format("Unknown instruction type: %s", this.node)
                );
        }
        return result;
    }
}
