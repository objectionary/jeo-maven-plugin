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
            Matchers.equalTo(
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
        AllLabels labels = new AllLabels();
        MatcherAssert.assertThat(
            "Can't create bytecode with method with array parameter",
            new BytecodeClass("ClassWithMethod")
                .withMethod(
                    "main",
                    "([Ljava/lang/String;)V",
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC
                )
                .opcode(Opcodes.ALOAD, 0)
                .opcode(Opcodes.ICONST_0)
                .opcode(Opcodes.AALOAD)
                .opcode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/Long",
                    "parseLong",
                    "(Ljava/lang/String;)J"
                )
                .opcode(Opcodes.LSTORE, 1)
                .opcode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
                .opcode(Opcodes.LDC, "Hello %d")
                .opcode(Opcodes.ICONST_1)
                .opcode(Opcodes.ANEWARRAY, "java/lang/Object")
                .opcode(Opcodes.ANEWARRAY, "java/lang/Object")
                .opcode(Opcodes.ICONST_0)
                .opcode(Opcodes.LLOAD, 1)
                .opcode(Opcodes.AASTORE)
                .opcode(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;")
                .opcode(
                    Opcodes.INVOKEVIRTUAL,
                    "java/io/PrintStream",
                    "printf",
                    "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;"
                )
                .opcode(Opcodes.RETURN)
                .up()
                .bytecode(),
            Matchers.notNullValue()
        );
//        final XML xml = new BytecodeClass("Wonders")
//            .withMethod(
//                "main1",
//                "([Ljava/lang/String;)V",
//                Opcodes.ACC_PUBLIC
//            )
//            .opcode(Opcodes.POP)
//            .opcode(Opcodes.LDC, "!")
//            .opcode(Opcodes.LDC, "!")
//            .opcode(Opcodes.LDC, "!")
//            .opcode(Opcodes.LDC, "!")
//            .opcode(Opcodes.LDC, "!")
//            .opcode(Opcodes.LDC, "!")
//            .opcode(Opcodes.RETURN)
//            .opcode(Opcodes.RETURN)
//            .opcode(Opcodes.RETURN)
//            .up()
//            .xml();

    }


//            <o base="opcode" line="999" name="GETSTATIC-4">
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 B2</o>
//                     <o base="string" data="bytes">6A 61 76 61 2F 6C 61 6E 67 2F 53 79 73 74 65 6D</o>
//                     <o base="string" data="bytes">6F 75 74</o>
//                     <o base="string" data="bytes">4C 6A 61 76 61 2F 69 6F 2F 50 72 69 6E 74 53 74 72 65 61 6D 3B</o>
//                  </o>
//                  <o base="opcode" line="999" name="LDC-5">
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 12</o>
//                     <o base="string" data="bytes">48 65 6C 6C 6F 20 25 64</o>
//                  </o>
//                  <o base="opcode" line="999" name="ICONST_1-6">
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 04</o>
//                  </o>
//                  <o base="opcode" line="999" name="ANEWARRAY-7">
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 BD</o>
//                     <o base="string" data="bytes">6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74</o>
//                  </o>
//                  <o base="opcode" line="999" name="ANEWARRAY-8">
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 BD</o>
//                     <o base="string" data="bytes">6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74</o>
//                  </o>
//                  <o base="opcode" line="999" name="ICONST_0-9">
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 03</o>
//                  </o>
//                  <o base="opcode" line="999" name="BIPUSH-A">
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 10</o>
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 0C</o>
//                  </o>
//                  <o base="opcode" line="999" name="AASTORE-B">
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 53</o>
//                  </o>
//                  <o base="opcode" line="999" name="INVOKESTATIC-C">
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 B8</o>
//                     <o base="string" data="bytes">6A 61 76 61 2F 6C 61 6E 67 2F 49 6E 74 65 67 65 72</o>
//                     <o base="string" data="bytes">76 61 6C 75 65 4F 66</o>
//                     <o base="string" data="bytes">28 49 29 4C 6A 61 76 61 2F 6C 61 6E 67 2F 49 6E 74 65 67 65 72 3B</o>
//                  </o>
//                  <o base="opcode" line="999" name="INVOKEVIRTUAL-D">
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 B6</o>
//                     <o base="string" data="bytes">6A 61 76 61 2F 69 6F 2F 50 72 69 6E 74 53 74 72 65 61 6D</o>
//                     <o base="string" data="bytes">70 72 69 6E 74 66</o>
//                     <o base="string" data="bytes">28 4C 6A 61 76 61 2F 6C 61 6E 67 2F 53 74 72 69 6E 67 3B 5B 4C 6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74 3B 29 4C 6A 61 76 61 2F 69 6F 2F 50 72 69 6E 74 53 74 72 65 61 6D 3B</o>
//                  </o>
//                  <o base="label">
//                     <o base="string" data="bytes">63 38 34 62 62 32 63 37 2D 61 34 33 63 2D 34 37 65 64 2D 39 64 38 33 2D 62 36 33 64 39 33 65 39 35 36 35 30</o>
//                  </o>
//                  <o base="opcode" line="999" name="RETURN-E">
//                     <o base="int" data="bytes">00 00 00 00 00 00 00 B1</o>
//                  </o>
//                  <o base="label">
//                     <o base="string" data="bytes">34 63 36 36 66 32 31 34 2D 66 64 32 64 2D 34 35 66 63 2D 39 62 64 36 2D 33 66 64 63 37 62 39 61 66 61 35 35</o>
//                  </o>

    @Test
    void someOtherTest() {
        new BytecodeClass("SomeClass")
            .withMethod(
                "main",
                "()V",
                Opcodes.ACC_PUBLIC
            )

            .opcode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .opcode(Opcodes.LDC, "Hello %d")
            .opcode(Opcodes.ICONST_1)
            .opcode(Opcodes.ANEWARRAY, "java/lang/Object")
            .opcode(Opcodes.ANEWARRAY, "java/lang/Object")
            .opcode(Opcodes.ICONST_0)
            .opcode(Opcodes.BIPUSH, 12)
            .opcode(Opcodes.AASTORE)
            .opcode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
            .opcode(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "printf",
                "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;"
            )
            .opcode(Opcodes.RETURN)
            .up()

            .xml();
    }

    @Test
    void oneMoreTest() {
        final AllLabels labels = new AllLabels();
        new BytecodeClass("org/eolang/benchmark/Main",new BytecodeClassProperties(33, null, "java/lang/Object", new String[0]))
            .withMethod("<init>", "()V", 1)
// Labels are not supported in tests yet
            .opcode(Opcodes.ALOAD, 0)
            .opcode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V")
            .opcode(Opcodes.RETURN)
// Labels are not supported in tests yet
            .up()
            .withMethod("j$main", "([Ljava/lang/String;)V", 137)
            .label(labels.label("1"))
            .opcode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .opcode(Opcodes.LDC, "Hello %d")
            .opcode(Opcodes.ICONST_1)
            .opcode(Opcodes.ANEWARRAY, "java/lang/Object")
            .opcode(Opcodes.ANEWARRAY, "java/lang/Object")
            .opcode(Opcodes.ICONST_0)
            .opcode(Opcodes.BIPUSH, 12)
            .opcode(Opcodes.AASTORE)
            .opcode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
            .opcode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "printf",
                "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;")
            .label(labels.label("2"))
            .opcode(Opcodes.RETURN)
            .label(labels.label("3"))
            .up()
            .bytecode();
    }

    @Test
    void nextTest(){
        new BytecodeClass("Some")
            .withMethod(
                "j$main", "([Ljava/lang/String;)V", 137
            )
            .label(new Label())
            .opcode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .opcode(Opcodes.LDC, "Hello %d")
            .opcode(Opcodes.ICONST_1)
            .opcode(Opcodes.ANEWARRAY, "java/lang/Object")
            .opcode(Opcodes.DUP)
            .opcode(Opcodes.ICONST_0)
            .opcode(Opcodes.BIPUSH, 12)
            .opcode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
            .opcode(Opcodes.AASTORE)
            .opcode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "printf", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;")
            .opcode(Opcodes.POP)
            .label(new Label())
            .opcode(Opcodes.RETURN)
            .label(new Label())
            .up();

//          opcode > GETSTATIC-27
//          178
//          "java/lang/System"
//          "out"
//          "Ljava/io/PrintStream;"
//        opcode > LDC-28
//          18
//          "Hello %d"
//        opcode > ICONST_1-29
//          4
//        opcode > ANEWARRAY-2A
//          189
//          "java/lang/Object"
//        opcode > DUP-2B
//          89
//        opcode > ICONST_0-2C
//          3
//        opcode > BIPUSH-2D
//          16
//          12
//        opcode > INVOKESTATIC-2E
//          184
//          "java/lang/Integer"
//          "valueOf"
//          "(I)Ljava/lang/Integer;"
//        opcode > AASTORE-2F
//          83
//        opcode > INVOKEVIRTUAL-30
//          182
//          "java/io/PrintStream"
//          "printf"
//          "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;"
//        opcode > POP-31
//          87
//        label
//          "133add3a-85c2-4f01-aad6-579e2fe190c1"
//        opcode > RETURN-32
//          177
//        label
//          "2608fdb3-05c0-42f1-aead-1622d368c16e"
    }
}
