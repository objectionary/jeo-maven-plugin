/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.Collections;
import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link BytecodeInstruction}.
 * @since 0.11.0
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
final class BytecodeInstructionTest {

    @Test
    void covertsInstructionWithTypeToDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the bytecode instruction argument with type 'Type' will be wrapped in sting, see https://github.com/objectionary/jeo-maven-plugin/issues/1125",
            new Xembler(
                new BytecodeInstruction(
                    Opcodes.LDC,
                    Type.getType(Integer.class)
                ).directives(0, new Format())
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesInstruction(
                        0,
                        new Format(),
                        Opcodes.LDC,
                        Type.getType(Integer.class)
                    )
                ).xml()
            )
        );
    }

    @Test
    void calculatesImpactForLdcLong() {
        MatcherAssert.assertThat(
            "LDC with a long constant pushes two slots",
            new BytecodeInstruction(Opcodes.LDC, 42L).impact(),
            Matchers.equalTo(2)
        );
    }

    @Test
    void calculatesImpactForLdcDouble() {
        MatcherAssert.assertThat(
            "LDC with a double constant pushes two slots",
            new BytecodeInstruction(Opcodes.LDC, 42.0d).impact(),
            Matchers.equalTo(2)
        );
    }

    @Test
    void calculatesImpactForLdcInteger() {
        MatcherAssert.assertThat(
            "LDC with an int constant pushes one slot",
            new BytecodeInstruction(Opcodes.LDC, 42).impact(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void calculatesImpactForLdcFloat() {
        MatcherAssert.assertThat(
            "LDC with a float constant pushes one slot",
            new BytecodeInstruction(Opcodes.LDC, 42.0f).impact(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void calculatesImpactForLdcString() {
        MatcherAssert.assertThat(
            "LDC with a string constant pushes one slot",
            new BytecodeInstruction(Opcodes.LDC, "answer").impact(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void calculatesImpactForLdcType() {
        MatcherAssert.assertThat(
            "LDC with a class constant pushes one slot",
            new BytecodeInstruction(Opcodes.LDC, Type.getType(Integer.class)).impact(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void calculatesImpactForLdcHandle() {
        MatcherAssert.assertThat(
            "LDC with a method handle pushes one slot",
            new BytecodeInstruction(
                Opcodes.LDC,
                new Handle(Opcodes.H_INVOKESTATIC, "java/lang/Math", "abs", "(I)I", false)
            ).impact(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void calculatesImpactForLdcNull() {
        MatcherAssert.assertThat(
            "LDC with a null constant pushes one slot",
            new BytecodeInstruction(Opcodes.LDC, (Object) null).impact(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void calculatesImpactForGetstatic() {
        MatcherAssert.assertThat(
            "GETSTATIC of an int field pushes one slot",
            new BytecodeInstruction(
                Opcodes.GETSTATIC,
                "owner",
                "name",
                "I"
            ).impact(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void calculatesImpactForPutstatic() {
        MatcherAssert.assertThat(
            "PUTSTATIC of an int field pops one slot",
            new BytecodeInstruction(
                Opcodes.PUTSTATIC,
                "owner",
                "name",
                "I"
            ).impact(),
            Matchers.equalTo(-1)
        );
    }

    @Test
    void calculatesImpactForGetfield() {
        MatcherAssert.assertThat(
            "GETFIELD pops the reference and pushes an int field",
            new BytecodeInstruction(
                Opcodes.GETFIELD,
                "owner",
                "name",
                "I"
            ).impact(),
            Matchers.equalTo(0)
        );
    }

    @Test
    void calculatesImpactForPutfield() {
        MatcherAssert.assertThat(
            "PUTFIELD pops the reference and the int field",
            new BytecodeInstruction(
                Opcodes.PUTFIELD,
                "owner",
                "name",
                "I"
            ).impact(),
            Matchers.equalTo(-2)
        );
    }

    @Test
    void calculatesImpactForInvokestatic() {
        MatcherAssert.assertThat(
            "INVOKESTATIC with (I)I pops one argument and pushes one result",
            new BytecodeInstruction(
                Opcodes.INVOKESTATIC,
                "owner",
                "name",
                "(I)I"
            ).impact(),
            Matchers.equalTo(0)
        );
    }

    @Test
    void calculatesImpactForInvokevirtual() {
        MatcherAssert.assertThat(
            "INVOKEVIRTUAL with (I)I additionally pops the receiver",
            new BytecodeInstruction(
                Opcodes.INVOKEVIRTUAL,
                "owner",
                "name",
                "(I)I"
            ).impact(),
            Matchers.equalTo(-1)
        );
    }

    @Test
    void calculatesImpactForMultiAnNewArray() {
        MatcherAssert.assertThat(
            "MULTIANEWARRAY with two dimensions pops two ints and pushes a reference",
            new BytecodeInstruction(Opcodes.MULTIANEWARRAY, "[[I", 2).impact(),
            Matchers.equalTo(-1)
        );
    }

    @Test
    void returnsJumpsForGoto() {
        final BytecodeLabel label = new BytecodeLabel("target");
        MatcherAssert.assertThat(
            "GOTO jumps to a single label",
            new BytecodeInstruction(Opcodes.GOTO, label).jumps(),
            Matchers.equalTo(Collections.singletonList(label))
        );
    }

    @Test
    void returnsJumpsForConditionalBranch() {
        final BytecodeLabel label = new BytecodeLabel("branch");
        MatcherAssert.assertThat(
            "IFEQ jumps to a single label",
            new BytecodeInstruction(Opcodes.IFEQ, label).jumps(),
            Matchers.equalTo(Collections.singletonList(label))
        );
    }

    @Test
    void returnsJumpsForTableSwitch() {
        final BytecodeLabel dflt = new BytecodeLabel("dflt");
        final BytecodeLabel single = new BytecodeLabel("single");
        MatcherAssert.assertThat(
            "TABLESWITCH jumps to the default and every case label",
            new BytecodeInstruction(Opcodes.TABLESWITCH, 0, 1, dflt, single).jumps(),
            Matchers.equalTo(Arrays.asList(dflt, single))
        );
    }

    @Test
    void recognizesGotoAsJump() {
        MatcherAssert.assertThat(
            "GOTO is a jump instruction",
            new BytecodeInstruction(Opcodes.GOTO, new BytecodeLabel("x")).isJump(),
            Matchers.is(true)
        );
    }

    @Test
    void doesNotRecognizeNopAsJump() {
        MatcherAssert.assertThat(
            "NOP is not a jump instruction",
            new BytecodeInstruction(Opcodes.NOP).isJump(),
            Matchers.is(false)
        );
    }

    @Test
    void recognizesIfeqAsIf() {
        MatcherAssert.assertThat(
            "IFEQ is a conditional branch",
            new BytecodeInstruction(Opcodes.IFEQ, new BytecodeLabel("x")).isIf(),
            Matchers.is(true)
        );
    }

    @Test
    void doesNotRecognizeGotoAsIf() {
        MatcherAssert.assertThat(
            "GOTO is not a conditional branch",
            new BytecodeInstruction(Opcodes.GOTO, new BytecodeLabel("x")).isIf(),
            Matchers.is(false)
        );
    }

    @Test
    void recognizesTableSwitchAsSwitch() {
        MatcherAssert.assertThat(
            "TABLESWITCH is a switch instruction",
            new BytecodeInstruction(Opcodes.TABLESWITCH).isSwitch(),
            Matchers.is(true)
        );
    }

    @Test
    void recognizesIreturnAsReturn() {
        MatcherAssert.assertThat(
            "IRETURN is a return instruction",
            new BytecodeInstruction(Opcodes.IRETURN).isReturn(),
            Matchers.is(true)
        );
    }

    @Test
    void doesNotRecognizeNopAsReturn() {
        MatcherAssert.assertThat(
            "NOP is not a return instruction",
            new BytecodeInstruction(Opcodes.NOP).isReturn(),
            Matchers.is(false)
        );
    }

    @Test
    void recognizesLoadAsVarInstruction() {
        MatcherAssert.assertThat(
            "ILOAD is a variable instruction",
            new BytecodeInstruction(Opcodes.ILOAD, 2).isVarInstruction(),
            Matchers.is(true)
        );
    }

    @Test
    void retrievesVarIndex() {
        MatcherAssert.assertThat(
            "The variable index of ILOAD is its second argument",
            new BytecodeInstruction(Opcodes.ILOAD, 2).varIndex(),
            Matchers.equalTo(2)
        );
    }

    @Test
    void retrievesVarSizeForInt() {
        MatcherAssert.assertThat(
            "An int local variable occupies one slot",
            new BytecodeInstruction(Opcodes.ILOAD, 2).varSize(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void retrievesVarSizeForLong() {
        MatcherAssert.assertThat(
            "A long local variable occupies two slots",
            new BytecodeInstruction(Opcodes.LLOAD, 2).varSize(),
            Matchers.equalTo(2)
        );
    }

    @Test
    void rejectsVarIndexForNonVarInstruction() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new BytecodeInstruction(Opcodes.NOP).varIndex(),
            "Expected an exception for a non-variable instruction"
        );
    }
}
