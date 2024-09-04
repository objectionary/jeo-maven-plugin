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
                "//o[@base='opcode' and @name='getstatic']",
                "//o[@base='opcode' and @name='ldc']",
                "//o[@base='opcode' and @name='invokevirtual']",
                "//o[@base='opcode' and @name='return']"
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

    @Test
    void triesToCompileTruncatedClassFromGenericsIntegrationTest() {
        final String application = "org/eolang/jeo/Application";
        final String descr = "()V";
        final String init = "<init>";
        final String developer = "org/eolang/jeo/Developer";
        Assertions.assertDoesNotThrow(
            () -> new BytecodeClass(application)
                .withMethod(init, descr, 1)
                .label("245a510e-d8c5-45d9-b270-921a5e17e9c8")
                .opcode(Opcodes.ALOAD, 0)
                .opcode(Opcodes.INVOKESPECIAL, "java/lang/Object", init, descr, false)
                .opcode(Opcodes.RETURN)
                .label("3a851f60-17cd-4ff6-a7be-cf3b0fe4b843")
                .up()
                .withMethod("j$main", "([Ljava/lang/String;)V", 9)
                .label("6c2d7242-c4be-4605-a330-2bde6dc4b423")
                .opcode(Opcodes.NEW, developer)
                .opcode(Opcodes.DUP)
                .opcode(Opcodes.LDC, "Yegor")
                .opcode(Opcodes.NEW, "org/eolang/jeo/Application$1")
                .opcode(Opcodes.DUP)
                .opcode(
                    Opcodes.INVOKESPECIAL, "org/eolang/jeo/Application$1", init, descr, false
                )
                .opcode(
                    Opcodes.INVOKESPECIAL,
                    developer,
                    init,
                    "(Ljava/lang/String;Lorg/eolang/jeo/Language;)V",
                    false
                )
                .label("a568f756-0140-44c9-bb48-1ed13d9058a5")
                .opcode(
                    Opcodes.INVOKEVIRTUAL, developer, "writeCode", descr, false
                )
                .label("1f3726bc-879d-4649-ad4e-2592064a4deb")
                .opcode(Opcodes.NEW, developer)
                .opcode(Opcodes.DUP)
                .opcode(Opcodes.LDC, "Lev")
                .opcode(Opcodes.NEW, "org/eolang/jeo/Application$2")
                .opcode(Opcodes.DUP)
                .opcode(
                    Opcodes.INVOKESPECIAL, "org/eolang/jeo/Application$2", init, descr, false
                )
                .opcode(
                    Opcodes.INVOKESPECIAL,
                    developer,
                    init,
                    "(Ljava/lang/String;Lorg/eolang/jeo/Language;)V",
                    false
                )
                .label("1751c688-5569-4ebc-97fc-ea9f118702ff")
                .opcode(
                    Opcodes.INVOKEVIRTUAL, developer, "writeCode", descr, false
                )
                .label("aa3d26fc-1d87-4898-9c6b-dd77d5bf2a37")
                .opcode(Opcodes.NEW, developer)
                .opcode(Opcodes.DUP)
                .opcode(Opcodes.LDC, "Maxim")
                .opcode(Opcodes.NEW, "org/eolang/jeo/Application$3")
                .opcode(Opcodes.DUP)
                .opcode(
                    Opcodes.INVOKESPECIAL, "org/eolang/jeo/Application$3", init, descr, false
                )
                .opcode(
                    Opcodes.INVOKESPECIAL,
                    developer,
                    init,
                    "(Ljava/lang/String;Lorg/eolang/jeo/Language;)V",
                    false
                )
                .label("5e0e53c5-464b-4073-b825-af00455fb1f9")
                .opcode(
                    Opcodes.INVOKEVIRTUAL, developer, "writeCode", descr, false
                )
                .label("7ed0c330-96db-481f-9d18-cdbaf53dbd66")
                .opcode(Opcodes.NEW, developer)
                .opcode(Opcodes.DUP)
                .opcode(Opcodes.LDC, "Roman")
                .opcode(Opcodes.NEW, "org/eolang/jeo/Application$4")
                .opcode(Opcodes.DUP)
                .opcode(
                    Opcodes.INVOKESPECIAL, "org/eolang/jeo/Application$4", init, descr, false
                )
                .opcode(
                    Opcodes.INVOKESPECIAL,
                    developer,
                    init,
                    "(Ljava/lang/String;Lorg/eolang/jeo/Language;)V",
                    false
                )
                .label("7c6d85b2-8938-485f-859d-7bc3bcac3b03")
                .opcode(
                    Opcodes.INVOKEVIRTUAL, developer, "writeCode", descr, false
                )
                .label("09d62634-f853-45d7-9c5d-80f3a2ecbfde")
                .opcode(Opcodes.RETURN)
                .label("0db9a0b5-5515-49b4-9cac-b733be49aef9")
                .up()
                .bytecode(),
            "We expect no exception here because all instructions are valid"
        );
    }
}
