/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeInstruction;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Opcodes;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesInstruction}.
 * @since 0.6
 */
final class DirectivesInstructionTest {

    @ParameterizedTest
    @MethodSource("instructions")
    void addsBeautifulComment(final BytecodeInstruction instr, final String comment) {
        MatcherAssert.assertThat(
            "We expect, that during transformation to XML, we will get a beautiful comment for bytecode instruction",
            new Xembler(instr.directives()).xmlQuietly(),
            Matchers.containsString(comment)
        );
    }

    /**
     * Test cases.
     * All generated cases are used in
     * {@link DirectivesInstructionTest#addsBeautifulComment(BytecodeInstruction, String)}.
     * @return Test cases.
     */
    static Stream<Arguments> instructions() {
        return Stream.of(
            Arguments.of(
                new BytecodeInstruction(
                    Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"
                ),
                "<!-- #183:invokespecial(java/lang/Object, &lt;init&gt;, ()V) -->"
            ),
            Arguments.of(
                new BytecodeInstruction(
                    Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;"
                ),
                "<!-- #182:invokevirtual(java/lang/Object, toString, ()Ljava/lang/String;) -->"
            ),
            Arguments.of(
                new BytecodeInstruction(
                    Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J"
                ),
                "<!-- #184:invokestatic(java/lang/System, currentTimeMillis, ()J) -->"
            ),
            Arguments.of(
                new BytecodeInstruction(
                    Opcodes.INVOKEINTERFACE, "java/util/List", "size", "()I"
                ),
                "<!-- #185:invokeinterface(java/util/List, size, ()I) -->"
            ),
            Arguments.of(
                new BytecodeInstruction(
                    Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I"
                ),
                "<!-- #182:invokevirtual(java/lang/String, length, ()I) -->"
            ),
            Arguments.of(new BytecodeInstruction(Opcodes.RETURN), "<!-- #177:return() -->"),
            Arguments.of(new BytecodeInstruction(Opcodes.ARETURN), "<!-- #176:areturn() -->"),
            Arguments.of(new BytecodeInstruction(Opcodes.DUP), "<!-- #89:dup() -->")
        );
    }
}
