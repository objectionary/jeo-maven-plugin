/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
final class BytecodeInstructionTest {

    @Test
    void convertsInstructionWithTypeToDirectives() throws ImpossibleModificationException {
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
}
