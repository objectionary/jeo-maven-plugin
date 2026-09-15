/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import org.eolang.jeo.representation.bytecode.BytecodeFrame;
import org.eolang.jeo.representation.bytecode.BytecodeInstruction;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.bytecode.BytecodeLine;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Test cases for {@link AsmInstruction}.
 * @since 0.6
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
final class AsmInstructionTest {

    @Test
    void convertsPlainInstruction() {
        MatcherAssert.assertThat(
            "We expect that a plain instruction is converted to a bytecode instruction",
            new AsmInstruction(new InsnNode(Opcodes.NOP)).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.NOP))
        );
    }

    @Test
    void convertsIntInstruction() {
        MatcherAssert.assertThat(
            "We expect that an int instruction is converted with its operand",
            new AsmInstruction(new IntInsnNode(Opcodes.BIPUSH, 5)).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.BIPUSH, 5))
        );
    }

    @Test
    void convertsVarInstruction() {
        MatcherAssert.assertThat(
            "We expect that a var instruction is converted with its index",
            new AsmInstruction(new VarInsnNode(Opcodes.ILOAD, 2)).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.ILOAD, 2))
        );
    }

    @Test
    void convertsTypeInstruction() {
        MatcherAssert.assertThat(
            "We expect that a type instruction is converted with its descriptor",
            new AsmInstruction(new TypeInsnNode(Opcodes.NEW, "java/lang/String")).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.NEW, "java/lang/String"))
        );
    }

    @Test
    void convertsFieldInstruction() {
        MatcherAssert.assertThat(
            "We expect that a field instruction is converted with owner, name and descriptor",
            new AsmInstruction(
                new FieldInsnNode(Opcodes.GETFIELD, "owner", "name", "I")
            ).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.GETFIELD, "owner", "name", "I"))
        );
    }

    @Test
    void convertsMethodInstruction() {
        MatcherAssert.assertThat(
            "We expect that a method instruction is converted with owner, name and descriptor",
            new AsmInstruction(
                new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "owner", "name", "()V", false)
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeInstruction(Opcodes.INVOKEVIRTUAL, "owner", "name", "()V", false)
            )
        );
    }

    @Test
    void convertsInvokeDynamicInstruction() {
        final Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, "owner", "bsm", "()V", false);
        MatcherAssert.assertThat(
            "We expect that an invoke dynamic instruction is converted with name, descriptor, bsm and arguments",
            new AsmInstruction(
                new InvokeDynamicInsnNode("name", "()V", bsm, 42)
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeInstruction(Opcodes.INVOKEDYNAMIC, "name", "()V", bsm, 42)
            )
        );
    }

    @Test
    void convertsJumpInstruction() {
        final LabelNode label = new LabelNode();
        MatcherAssert.assertThat(
            "We expect that a jump instruction is converted with its target label",
            new AsmInstruction(new JumpInsnNode(Opcodes.GOTO, label)).bytecode(),
            Matchers.equalTo(
                new BytecodeInstruction(
                    Opcodes.GOTO,
                    new BytecodeLabel(label.getLabel().toString())
                )
            )
        );
    }

    @Test
    void convertsLabel() {
        final LabelNode label = new LabelNode();
        MatcherAssert.assertThat(
            "We expect that a label is converted to a bytecode label",
            new AsmInstruction(label).bytecode(),
            Matchers.equalTo(new BytecodeLabel(label.getLabel().toString()))
        );
    }

    @Test
    void convertsLdcInstruction() {
        MatcherAssert.assertThat(
            "We expect that an LDC instruction is converted with its constant",
            new AsmInstruction(new LdcInsnNode("const")).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.LDC, "const"))
        );
    }

    @Test
    void convertsIincInstruction() {
        MatcherAssert.assertThat(
            "We expect that an IINC instruction is converted with its index and increment",
            new AsmInstruction(new IincInsnNode(1, 2)).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.IINC, 1, 2))
        );
    }

    @Test
    void convertsTableSwitchInstruction() {
        final LabelNode dflt = new LabelNode();
        final LabelNode single = new LabelNode();
        MatcherAssert.assertThat(
            "We expect that a table switch instruction is converted with min, max and labels",
            new AsmInstruction(
                new TableSwitchInsnNode(0, 1, dflt, single)
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeInstruction(
                    Opcodes.TABLESWITCH,
                    0,
                    1,
                    new BytecodeLabel(dflt.getLabel().toString()),
                    new BytecodeLabel(single.getLabel().toString())
                )
            )
        );
    }

    @Test
    void convertsLookupSwitchInstruction() {
        final LabelNode dflt = new LabelNode();
        final LabelNode first = new LabelNode();
        final LabelNode second = new LabelNode();
        MatcherAssert.assertThat(
            "We expect that a lookup switch instruction is converted with its keys and labels",
            new AsmInstruction(
                new LookupSwitchInsnNode(
                    dflt,
                    new int[] {1, 2},
                    new LabelNode[] {first, second}
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeInstruction(
                    Opcodes.LOOKUPSWITCH,
                    new BytecodeLabel(dflt.getLabel().toString()),
                    1,
                    2,
                    new BytecodeLabel(first.getLabel().toString()),
                    new BytecodeLabel(second.getLabel().toString())
                )
            )
        );
    }

    @Test
    void convertsMultiAnNewArrayInstruction() {
        MatcherAssert.assertThat(
            "We expect that a multi array instruction is converted with its descriptor and dimensions",
            new AsmInstruction(new MultiANewArrayInsnNode("[[I", 2)).bytecode(),
            Matchers.equalTo(new BytecodeInstruction(Opcodes.MULTIANEWARRAY, "[[I", 2))
        );
    }

    @Test
    void convertsFrame() {
        MatcherAssert.assertThat(
            "We expect that a frame is converted to a bytecode frame",
            new AsmInstruction(
                new FrameNode(Opcodes.F_NEW, 0, null, 0, null)
            ).bytecode(),
            Matchers.equalTo(new BytecodeFrame(Opcodes.F_NEW, 0, new Object[0], 0))
        );
    }

    @Test
    void convertsLine() {
        final LabelNode label = new LabelNode();
        MatcherAssert.assertThat(
            "We expect that a line number is converted to a bytecode line",
            new AsmInstruction(new LineNumberNode(10, label)).bytecode(),
            Matchers.equalTo(
                new BytecodeLine(10, new BytecodeLabel(label.getLabel().toString()))
            )
        );
    }
}
