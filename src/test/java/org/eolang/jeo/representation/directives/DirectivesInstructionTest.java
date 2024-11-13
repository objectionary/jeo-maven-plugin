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
            "We expect, that during convertation to XML, we will get a beautiful comment for bytecode instruction",
            new Xembler(instr.directives(false)).xmlQuietly(),
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
                "<!--#183:invokespecial(java/lang/Object, &lt;init&gt;, ()V)-->"
            ),
            Arguments.of(
                new BytecodeInstruction(
                    Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;"
                ),
                "<!--#182:invokevirtual(java/lang/Object, toString, ()Ljava/lang/String;)-->"
            ),
            Arguments.of(
                new BytecodeInstruction(
                    Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J"
                ),
                "<!--#184:invokestatic(java/lang/System, currentTimeMillis, ()J)-->"
            ),
            Arguments.of(
                new BytecodeInstruction(
                    Opcodes.INVOKEINTERFACE, "java/util/List", "size", "()I"
                ),
                "<!--#185:invokeinterface(java/util/List, size, ()I)-->"
            ),
            Arguments.of(
                new BytecodeInstruction(
                    Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I"
                ),
                "<!--#182:invokevirtual(java/lang/String, length, ()I)-->"
            ),
            Arguments.of(new BytecodeInstruction(Opcodes.RETURN), "<!--#177:return()-->"),
            Arguments.of(new BytecodeInstruction(Opcodes.ARETURN), "<!--#176:areturn()-->"),
            Arguments.of(new BytecodeInstruction(Opcodes.DUP), "<!--#89:dup()-->")
        );
    }
}
