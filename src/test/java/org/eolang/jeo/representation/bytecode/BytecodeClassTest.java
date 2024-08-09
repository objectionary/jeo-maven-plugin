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
import org.eolang.jeo.representation.xmir.AllLabels;
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
final class BytecodeClassTest {

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
                .withMethod("j$bar", "()I", Opcodes.ACC_PUBLIC)
                .label(new Label())
                .opcode(Opcodes.ALOAD, 0)
                .opcode(Opcodes.INVOKEVIRTUAL, "com/exam/BA", "foo", "()I")
                .opcode(Opcodes.ICONST_2)
                .opcode(Opcodes.IADD)
                .opcode(Opcodes.IRETURN)
                .label(new Label())
                .up()
                .bytecode(),
            "We expect an exception here because the bytecode is broken"
        );
    }

    @Test
    void returnsShortValue() {
        Assertions.assertDoesNotThrow(
            () -> new BytecodeClass("ShortValue")
                .withMethod("j$foo", "()S", Opcodes.ACC_PUBLIC)
                .opcode(Opcodes.SIPUSH, 256)
                .opcode(Opcodes.IRETURN)
                .up()
                .bytecode(),
            "We expect no exception here because all instructions are valid"
        );
    }
//
//    @Test
//        //todo
//    void todo() {
//        AllLabels labels = new AllLabels();
//        new BytecodeClass("org/eolang/jeo/Application")
//            .withMethod("<init>", "()V", 1)
////max stack: 1 max locals 1
//            .label("3e03df94-823b-43e7-a0c5-f4300d326f2b")
//            .opcode(Opcodes.ALOAD, 0)
//            .opcode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
//            .opcode(Opcodes.RETURN)
//            .label("8bdd3057-883e-485e-b16f-4a9e4aefa28c")
//            .up()
//            .withMethod("j$main", "([Ljava/lang/String;)V", 9)
////max stack: 5 max locals 1
//            .label("b442b29c-4f6e-4049-8f22-df37fcdb133f")
//            .opcode(Opcodes.NEW, "org/eolang/jeo/Developer")
//            .opcode(Opcodes.DUP)
//            .opcode(Opcodes.LDC, "Yegor")
//            .opcode(Opcodes.NEW, "org/eolang/jeo/Application$1")
//            .opcode(Opcodes.DUP)
//            .opcode(Opcodes.INVOKESPECIAL, "org/eolang/jeo/Application$1", "<init>", "()V", false)
//            .opcode(
//                Opcodes.INVOKESPECIAL, "org/eolang/jeo/Developer", "<init>",
//                "(Ljava/lang/String;Lorg/eolang/jeo/Language;)V", false
//            )
//            .label("ce419e4e-bcd4-4e1b-9556-afd9afefcdff")
//            .opcode(Opcodes.INVOKEVIRTUAL, "org/eolang/jeo/Developer", "writeCode", "()V", false)
//            .label("e3b79e91-25c2-4394-9e0b-a45c8561ae2a")
//            .opcode(Opcodes.NEW, "org/eolang/jeo/Developer")
//            .opcode(Opcodes.DUP)
//            .opcode(Opcodes.LDC, "Lev")
//            .opcode(Opcodes.NEW, "org/eolang/jeo/Application$2")
//            .opcode(Opcodes.DUP)
//            .opcode(Opcodes.INVOKESPECIAL, "org/eolang/jeo/Application$2", "<init>", "()V", false)
//            .opcode(
//                Opcodes.INVOKESPECIAL, "org/eolang/jeo/Developer", "<init>",
//                "(Ljava/lang/String;Lorg/eolang/jeo/Language;)V", false
//            )
//            .label("8761227d-ef96-46cb-9a11-9ada0faece0c")
//            .opcode(Opcodes.INVOKEVIRTUAL, "org/eolang/jeo/Developer", "writeCode", "()V", false)
//            .label("0f127603-dda1-4c69-8287-b20f329098e5")
//            .opcode(Opcodes.NEW, "org/eolang/jeo/Developer")
//            .opcode(Opcodes.DUP)
//            .opcode(Opcodes.LDC, "Maxim")
//            .opcode(Opcodes.NEW, "org/eolang/jeo/Application$3")
//            .opcode(Opcodes.DUP)
//            .opcode(Opcodes.INVOKESPECIAL, "org/eolang/jeo/Application$3", "<init>", "()V", false)
//            .opcode(
//                Opcodes.INVOKESPECIAL, "org/eolang/jeo/Developer", "<init>",
//                "(Ljava/lang/String;Lorg/eolang/jeo/Language;)V", false
//            )
//            .label("eb6aeab8-729a-4331-8ee4-e5728e6595aa")
//            .opcode(Opcodes.INVOKEVIRTUAL, "org/eolang/jeo/Developer", "writeCode", "()V", false)
//            .label("c4f4ac71-cfe9-4d1f-bb91-e7dcf6a7c9e5")
//            .opcode(Opcodes.NEW, "org/eolang/jeo/Developer")
//            .opcode(Opcodes.DUP)
//            .opcode(Opcodes.LDC, "Roman")
//            .opcode(Opcodes.NEW, "org/eolang/jeo/Application$4")
//            .opcode(Opcodes.DUP)
//            .opcode(Opcodes.INVOKESPECIAL, "org/eolang/jeo/Application$4", "<init>", "()V", false)
//            .opcode(
//                Opcodes.INVOKESPECIAL, "org/eolang/jeo/Developer", "<init>",
//                "(Ljava/lang/String;Lorg/eolang/jeo/Language;)V", false
//            )
//            .label("24480cc6-b27e-4a6b-b349-5f0b46d4807d")
//            .opcode(Opcodes.INVOKEVIRTUAL, "org/eolang/jeo/Developer", "writeCode", "()V", false)
//            .label("ae047567-99f6-4f8b-a439-1c5447dddd32")
//            .opcode(Opcodes.RETURN)
//            .label("390be580-8c6f-4352-922c-142ca2846815")
//            .up()
//            .bytecode();
//    }
}
