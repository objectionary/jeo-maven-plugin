/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeInstruction;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesInstruction}.
 * @since 0.6
 */
final class DirectivesInstructionTest {

    @Test
    void covertsInstructionWithTypeToDirectives() {
        MatcherAssert.assertThat(
            "We expect that the bytecode instruction argument with type 'Type' will be wrapped in sting, see https://github.com/objectionary/jeo-maven-plugin/issues/1125",
            new Xembler(
                new DirectivesInstruction(
                    0,
                    new Format(),
                    Opcodes.LDC,
                    Type.getType(Integer.class)
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "ldc").toXpath(),
                new JeoBaseXpath("/o/o", "type").toXpath(),
                "/o/o/o[contains(@base,'string')]",
                "/o/o/o/o[contains(@base, 'bytes')]"
            )
        );
    }

    @ParameterizedTest
    @MethodSource("instructionBases")
    void transformsIntoEoWithoutCountingOpcodes(
        final int opcode, final String base
    ) throws ImpossibleModificationException {
        final String xml = new Xembler(new DirectivesInstruction(0, new Format(), opcode)).xml();
        MatcherAssert.assertThat(
            String.format(
                "We expect to get the EO representation of the bytecode where each instruction has a simple name without sequence number, please check the final XML:%n%s%n",
                xml
            ),
            xml,
            XhtmlMatchers.hasXPath(new JeoBaseXpath("//o", base).toXpath())
        );
    }

    @ParameterizedTest
    @MethodSource("instructions")
    void addsBeautifulComment(final BytecodeInstruction instr, final String comment) {
        MatcherAssert.assertThat(
            "We expect, that during transformation to XML, we will get a beautiful comment for bytecode instruction",
            new Xembler(instr.directives(0, new Format())).xmlQuietly(),
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

    static Stream<Arguments> instructionBases() {
        return Stream.of(
            Arguments.of(Opcodes.GETSTATIC, "jeo.opcode.getstatic"),
            Arguments.of(Opcodes.LDC, "jeo.opcode.ldc"),
            Arguments.of(Opcodes.INVOKEVIRTUAL, "jeo.opcode.invokevirtual"),
            Arguments.of(Opcodes.RETURN, "jeo.opcode.return")
        );
    }
}
