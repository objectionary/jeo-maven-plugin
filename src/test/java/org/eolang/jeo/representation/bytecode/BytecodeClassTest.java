/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link org.eolang.jeo.representation.bytecode.BytecodeClass}.
 * @since 0.1
 */
class BytecodeClassTest {

    @Test
    void parsesAbstractClass() {
        final int access = Opcodes.ACC_ABSTRACT | Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER;
        MatcherAssert.assertThat(
            String.format(
                "Can't parse abstract class with access modifier %s",
                access
            ),
            new BytecodeClass("AbstractClass", access).bytecode(),
            Matchers.notNullValue()
        );
    }

    @Test
    void createsConstructorWithStoreInstructions() {
        Assertions.assertDoesNotThrow(
            () -> new BytecodeClass("Store")
                .withConstructor()
                .opcode(Opcodes.ICONST_0)
                .opcode(Opcodes.ISTORE, 1)
                .opcode(Opcodes.LCONST_0)
                .opcode(Opcodes.LSTORE, 2)
                .opcode(Opcodes.FCONST_0)
                .opcode(Opcodes.FSTORE, 3)
                .opcode(Opcodes.DCONST_0)
                .opcode(Opcodes.DSTORE, 4)
                .opcode(Opcodes.ACONST_NULL)
                .opcode(Opcodes.ASTORE, 5)
                .opcode(Opcodes.RETURN)
                .up()
                .bytecode(),
            "We expect no exception here because all instructions are valid"
        );
    }

    @Test
    void createsBytecodeWithDefaultConstructor() {
        MatcherAssert.assertThat(
            "Can't create bytecode with default public constructor",
            new BytecodeClass("DefaultConstructor")
                .withConstructor(Opcodes.ACC_PUBLIC)
                .opcode(Opcodes.RETURN)
                .up()
                .bytecode(),
            Matchers.notNullValue()
        );
    }

    @Test
    void createsClassWithUnknownInstruction() {
        MatcherAssert.assertThat(
            "Exception message is not equal to expected",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new BytecodeClass("UnknownInstruction")
                    .withConstructor()
                    .opcode(305)
                    .up()
                    .bytecode(),
                "We expect an exception here because 305 is not a valid opcode"
            ).getMessage(),
            Matchers.containsString(
                "Bytecode creation for the 'UnknownInstruction' class is not possible due to unmet preconditions."
            )
        );
    }

    @Test
    void transformsBytecodeIntoEoWithoutCountingOpcodes() {
        final XML xmir = new BytecodeRepresentation(
            new BytecodeClass("Hello")
                .helloWorldMethod()
                .bytecode()
        ).toEO(false);
        MatcherAssert.assertThat(
            String.format(
                "We expect to get the EO representation of the bytecode where each instruction has a simple name without sequence number, please check the final XML:%n%s%n",
                xmir
            ),
            xmir.toString(),
            XhtmlMatchers.hasXPaths(
                "//o[@base='opcode' and @name='GETSTATIC']",
                "//o[@base='opcode' and @name='LDC']",
                "//o[@base='opcode' and @name='INVOKEVIRTUAL']",
                "//o[@base='opcode' and @name='RETURN']"
            )
        );
    }

    @Test
    void createsMethodWithArrayParameterUsage() {
        Assertions.assertDoesNotThrow(
            () -> {
                new BytecodeClass("Some")
                    .withMethod("j$main", "([Ljava/lang/String;)V", 137)
                    .label(new Label())
                    .opcode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
                    .opcode(Opcodes.LDC, "Hello %d")
                    .opcode(Opcodes.ICONST_1)
                    .opcode(Opcodes.ANEWARRAY, "java/lang/Object")
                    .opcode(Opcodes.DUP)
                    .opcode(Opcodes.ICONST_0)
                    .opcode(Opcodes.BIPUSH, 12)
                    .opcode(
                        Opcodes.INVOKESTATIC,
                        "java/lang/Integer",
                        "valueOf",
                        "(I)Ljava/lang/Integer;"
                    )
                    .opcode(Opcodes.AASTORE)
                    .opcode(
                        Opcodes.INVOKEVIRTUAL,
                        "java/io/PrintStream",
                        "printf",
                        "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;"
                    )
                    .opcode(Opcodes.POP)
                    .label(new Label())
                    .opcode(Opcodes.RETURN)
                    .label(new Label())
                    .up();
            }, "We expect no exception here because all instructions are valid"
        );
    }

    @Test
    void generatesCodeForInterface() {
        Assertions.assertDoesNotThrow(
            () -> new BytecodeClass("org/eolang/benchmark/F")
                .withMethod("j$foo", "()I", 1025)
                .up()
                .bytecode(),
            "We expect no exception here because all instructions are valid. This is an abstract method."
        );
    }

    @Test
    void failsBecauseBytecodeIsBroken() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new BytecodeClass("Broken")
                .withMethod("j$bar", "()I", 0)
                .label("70b56006-856e-4ac2-be99-632ca25a65a0")
                .opcode(Opcodes.ALOAD, 0)
                .opcode(Opcodes.INVOKEVIRTUAL, "com/exam/BA", "foo", "()I")
                .opcode(Opcodes.ICONST_2)
                .opcode(Opcodes.IADD)
                .opcode(Opcodes.IRETURN)
                .label("f3d973ab-c502-4134-8d6f-d7fe89defc6e")
                .up()
                .bytecode(),
            "We expect an exception here because the bytecode is broken"
        );
    }
}
