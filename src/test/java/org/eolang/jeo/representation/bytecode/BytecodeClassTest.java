/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link org.eolang.jeo.representation.bytecode.BytecodeClass}.
 * @since 0.1
 */
@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.TooManyMethods"})
final class BytecodeClassTest {

    @Test
    void cleansMethods() {
        final String name = "CleanMethods";
        MatcherAssert.assertThat(
            String.format(
                "Can't clean methods from the class %s",
                name
            ),
            new BytecodeClass(name)
                .withMethod("j$foo", "()V", Opcodes.ACC_PUBLIC)
                .up()
                .withoutMethods(),
            Matchers.equalTo(new BytecodeClass(name))
        );
    }

    @Test
    void parsesAbstractClass() {
        final int access = Opcodes.ACC_ABSTRACT | Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER;
        MatcherAssert.assertThat(
            String.format(
                "Can't parse abstract class with access modifier %s",
                access
            ),
            new BytecodeProgram(new BytecodeClass("AbstractClass", access)).bytecode(),
            Matchers.notNullValue()
        );
    }

    @Test
    void createsConstructorWithStoreInstructions() {
        Assertions.assertDoesNotThrow(
            () -> new BytecodeProgram(
                new BytecodeClass("Store")
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
            ).bytecode(),
            "We expect no exception here because all instructions are valid"
        );
    }

    @Test
    void createsBytecodeWithDefaultConstructor() {
        MatcherAssert.assertThat(
            "Can't create bytecode with default public constructor",
            new BytecodeProgram(
                new BytecodeClass("DefaultConstructor")
                    .withConstructor(Opcodes.ACC_PUBLIC)
                    .opcode(Opcodes.RETURN)
                    .up()
            ).bytecode(),
            Matchers.notNullValue()
        );
    }

    @Test
    void createsClassWithUnknownInstruction() {
        MatcherAssert.assertThat(
            "Exception message is not equal to expected",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new BytecodeProgram(new BytecodeClass("UnknownInstruction")
                    .withConstructor()
                    .opcode(305)
                    .up()
                ).bytecode(),
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
            new BytecodeProgram(
                new BytecodeClass("Hello").helloWorldMethod()
            ).bytecode()
        ).toXmir();
        MatcherAssert.assertThat(
            String.format(
                "We expect to get the EO representation of the bytecode where each instruction has a simple name without sequence number, please check the final XML:%n%s%n",
                xmir
            ),
            xmir.toString(),
            XhtmlMatchers.hasXPaths(
                "//o[contains(@base,'jeo.opcode.getstatic')]",
                "//o[contains(@base,'jeo.opcode.ldc')]",
                "//o[contains(@base,'jeo.opcode.invokevirtual')]",
                "//o[contains(@base,'jeo.opcode.return')]"
            )
        );
    }

    @Test
    void createsMethodWithArrayParameterUsage() {
        Assertions.assertDoesNotThrow(
            () -> {
                new BytecodeClass("Some")
                    .withMethod("j$main", "([Ljava/lang/String;)V", 137)
                    .label()
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
                    .label()
                    .opcode(Opcodes.RETURN)
                    .label()
                    .up();
            }, "We expect no exception here because all instructions are valid"
        );
    }

    @Test
    void generatesCodeForInterface() {
        Assertions.assertDoesNotThrow(
            () -> new BytecodeProgram(new BytecodeClass("org/eolang/benchmark/F")
                .withMethod("j$foo", "()I", 1025)
                .up()
            ).bytecode(),
            "We expect no exception here because all instructions are valid. This is an abstract method."
        );
    }

    @Test
    void failsBecauseBytecodeIsBroken() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new BytecodeProgram(new BytecodeClass("Broken")
                .withMethod("j$bar", "()I", Opcodes.ACC_PUBLIC)
                .label()
                .opcode(Opcodes.ALOAD, 0)
                .opcode(Opcodes.INVOKEVIRTUAL, "com/exam/BA", "foo", "()I")
                .opcode(Opcodes.ICONST_2)
                .opcode(Opcodes.IADD)
                .opcode(Opcodes.IRETURN)
                .label()
                .up()
            ).bytecode(),
            "We expect an exception here because the bytecode is broken"
        );
    }

    @Test
    void returnsShortValue() {
        Assertions.assertDoesNotThrow(
            () -> new BytecodeProgram(
                new BytecodeClass("ShortValue")
                    .withMethod("j$foo", "()S", Opcodes.ACC_PUBLIC)
                    .opcode(Opcodes.SIPUSH, 256)
                    .opcode(Opcodes.IRETURN)
                    .up()
            ).bytecode(),
            "We expect no exception here because all instructions are valid"
        );
    }
}
