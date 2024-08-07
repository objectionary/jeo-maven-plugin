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
package org.eolang.jeo.representation.directives;

import com.jcabi.xml.XMLDocument;
import java.util.UUID;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.bytecode.BytecodeTryCatchBlock;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesMethodVisitor}.
 * We create {@link DirectivesMethodVisitor} only in the context
 * of using {@link DirectivesClassVisitor} in other words,
 * {@link DirectivesMethodVisitor} can't be createad
 * without {@link DirectivesClassVisitor} and it is the main
 * reason why in all the test we create
 * {@link DirectivesClassVisitor}.
 *
 * @since 0.1.0
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
final class DirectivesMethodVisitorTest {

    @Test
    void parsesMethodInstructions() throws ImpossibleModificationException {
        final DirectivesClassVisitor visitor = new DirectivesClassVisitor();
        new ClassReader(
            new BytecodeClass()
                .withMethod(new BytecodeMethodProperties("main", "()I"))
                .opcode(Opcodes.BIPUSH, 28)
                .opcode(Opcodes.IRETURN)
                .up()
                .bytecode()
                .asBytes()
        ).accept(visitor, 0);
        final String xml = new Xembler(visitor).xml();
        MatcherAssert.assertThat(
            String.format(
                "Can't find a method in the final XML by using XPath. Please, check the resulting XMIR: \n%s\n",
                xml
            ),
            xml,
            new HasMethod("main")
                .inside("Simple")
                .withInstruction(Opcodes.BIPUSH, 28)
                .withInstruction(Opcodes.IRETURN)
        );
    }

    /**
     * In this test we parse the next java code (but represented as bytecode).
     *
     * <p>
     *     {@code
     *     public class ParamsExample {
     *       public static void main(String[] args) {
     *         ParamsExample pe = new ParamsExample();
     *         pe.printSum(10, 20);
     *       }
     *       public void printSum(int a, int b) {
     *         int sum = a + b;
     *         System.out.println(sum);
     *       }
     *     }
     *     }
     * </p>
     */
    @Test
    void parsesMethodParameters() {
        final String clazz = "ParametersExample";
        final String method = "printSum";
        final String xml = new BytecodeClass(clazz)
            .withMethod(
                new BytecodeMethodProperties(
                    "main",
                    "([Ljava/lang/String;)V",
                    Opcodes.ACC_PUBLIC,
                    Opcodes.ACC_STATIC
                )
            )
            .opcode(Opcodes.NEW, clazz)
            .opcode(Opcodes.DUP)
            .opcode(Opcodes.INVOKESPECIAL, clazz, "<init>", "()V", false)
            .opcode(Opcodes.ASTORE, 1)
            .opcode(Opcodes.ALOAD, 1)
            .opcode(Opcodes.BIPUSH, 10)
            .opcode(Opcodes.BIPUSH, 20)
            .opcode(Opcodes.INVOKEVIRTUAL, clazz, method, "(II)V", false)
            .opcode(Opcodes.RETURN)
            .up()
            .withMethod(new BytecodeMethodProperties(method, "(II)V", Opcodes.ACC_PUBLIC))
            .opcode(Opcodes.ILOAD, 1)
            .opcode(Opcodes.ILOAD, 2)
            .opcode(Opcodes.IADD)
            .opcode(Opcodes.ISTORE, 3)
            .opcode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .opcode(Opcodes.ILOAD, 3)
            .opcode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false)
            .opcode(Opcodes.RETURN)
            .up()
            .xml()
            .toString();
        MatcherAssert.assertThat(
            String.format(
                "DirectivesMethod doesn't parse method parameters correctly. Please, check the resulting XMIR: \n%s\n",
                xml
            ),
            xml,
            new HasMethod(method)
                .inside(clazz)
                .withParameter("I")
                .withParameter("I")
        );
    }

    /**
     * In this test we parse the next java code.
     *
     * <p>
     *     {@code
     *     public class ConstructorExample {
     *       public ConstructorExample() {
     *           System.out.println("Hello, constructor!");
     *       }
     *       public static void main(String[] args) {
     *         new ConstructorExample();
     *       }
     *     }
     *     }
     * </p>
     */
    @Test
    void parsesConstructor() {
        final String xml = new BytecodeClass("ConstructorExample")
            .withConstructor(Opcodes.ACC_PUBLIC)
            .opcode(Opcodes.ALOAD, 0)
            .opcode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
            .opcode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .opcode(Opcodes.LDC, "Hello, constructor!")
            .opcode(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false
            )
            .opcode(Opcodes.RETURN)
            .up()
            .withMethod(
                new BytecodeMethodProperties(
                    "main",
                    "([Ljava/lang/String;)V",
                    Opcodes.ACC_PUBLIC,
                    Opcodes.ACC_STATIC
                )
            )
            .opcode(Opcodes.NEW, "ConstructorExample")
            .opcode(Opcodes.DUP)
            .opcode(Opcodes.INVOKESPECIAL, "ConstructorExample", "<init>", "()V", false)
            .opcode(Opcodes.POP)
            .opcode(Opcodes.RETURN)
            .up()
            .xml()
            .toString();
        MatcherAssert.assertThat(
            String.format(
                "Constructor wasn't parsed correctly, please, check the resulting XMIR: \n%s\n",
                new XMLDocument(xml)
            ),
            xml,
            new HasMethod("new")
                .inside("ConstructorExample")
                .withInstruction(Opcodes.LDC, "Hello, constructor!")
        );
    }

    /**
     * In this test we parse the next java code.
     *
     * <p>
     *     {@code
     *     public class ConstructorParams {
     *       public ConstructorParams(int a, int b)
     *           System.out.println(a + b);
     *       }
     *       public static void main(String[] args) {
     *         new ConstructorExample(11, 22);
     *       }
     *     }
     *     }
     * </p>
     */
    @Test
    void parsesConstructorWithParameters() {
        final String clazz = "ConstructorParams";
        final String xml = new BytecodeClass(clazz)
            .withConstructor("(II)V", Opcodes.ACC_PUBLIC)
            .opcode(Opcodes.ALOAD, 0)
            .opcode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
            .opcode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .opcode(Opcodes.ILOAD, 1)
            .opcode(Opcodes.ILOAD, 2)
            .opcode(Opcodes.IADD)
            .opcode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false)
            .opcode(Opcodes.RETURN)
            .up()
            .withMethod(
                new BytecodeMethodProperties(
                    "main",
                    "([Ljava/lang/String;)V",
                    Opcodes.ACC_PUBLIC,
                    Opcodes.ACC_STATIC
                )
            )
            .opcode(Opcodes.NEW, clazz)
            .opcode(Opcodes.DUP)
            .opcode(Opcodes.BIPUSH, 11)
            .opcode(Opcodes.BIPUSH, 22)
            .opcode(Opcodes.INVOKESPECIAL, clazz, "<init>", "(II)V", false)
            .opcode(Opcodes.POP)
            .opcode(Opcodes.RETURN)
            .up()
            .xml()
            .toString();
        MatcherAssert.assertThat(
            String.format(
                "Constructor wasn't parsed correctly, please, check the resulting XMIR: \n%s\n",
                new XMLDocument(xml)
            ),
            xml,
            new HasMethod("new")
                .inside(clazz)
                .withInstruction(Opcodes.ILOAD, 1)
                .withInstruction(Opcodes.ILOAD, 2)
                .withInstruction(Opcodes.IADD)
        );
    }

    /**
     * In this class we parse the next java code.
     * <p>
     * {@code
     *     class Foo {
     *       int bar(double x) {
     *         if (x > 0.0d) {
     *           return 5;
     *         }
     *         return 8;
     *       }
     *     }
     * }
     * </p>
     * Pay Attention! That we just can't verify exact id's of labels since ASM library doesn't
     * allow it. Hence, we can just check the presence of a label.
     */
    @Test
    void parsesIfStatementCorrectly() {
        final AllLabels labels = new AllLabels();
        final Label label = labels.label(UUID.randomUUID().toString());
        final String xml = new BytecodeClass("Foo")
            .withMethod("bar", "(D)I", 0)
            .opcode(Opcodes.DLOAD, 1)
            .opcode(Opcodes.DCONST_0)
            .opcode(Opcodes.DCMPL)
            .opcode(Opcodes.IFLE, label)
            .opcode(Opcodes.ICONST_5)
            .opcode(Opcodes.IRETURN)
            .label(label)
            .opcode(Opcodes.BIPUSH, 8)
            .opcode(Opcodes.IRETURN).up()
            .xml()
            .toString();
        MatcherAssert.assertThat(
            String.format(
                "If statement wasn't parsed correctly, please, check the resulting XMIR: \n%s\n",
                xml
            ),
            xml,
            new HasMethod("bar")
                .inside("Foo")
                .withInstruction(Opcodes.IFLE, label)
                .withLabel()
        );
    }

    /**
     * In this class we parse the next java code.
     * <p>
     * {@code
     *   public class Foo {
     *     public void bar(){
     *         try {
     *           throw new Exception();
     *         } catch (Exception e) {
     *         }
     *     }
     *   }
     * }
     */
    @Test
    void parsesTryCatchInstructions() {
        final AllLabels labels = new AllLabels();
        final Label start = labels.label(UUID.randomUUID().toString());
        final Label end = labels.label(UUID.randomUUID().toString());
        final Label handler = labels.label(UUID.randomUUID().toString());
        final String xml = new BytecodeClass("Foo")
            .withMethod("bar", "()V", Opcodes.ACC_PUBLIC)
            .label(start)
            .opcode(Opcodes.NEW, "java/lang/Exception")
            .opcode(Opcodes.DUP)
            .opcode(Opcodes.INVOKESPECIAL, "java/lang/Exception", "<init>", "()V", false)
            .opcode(Opcodes.ATHROW)
            .opcode(Opcodes.ASTORE, 1)
            .label(end)
            .label(handler)
            .opcode(Opcodes.RETURN)
            .trycatch(new BytecodeTryCatchBlock(start, end, handler, "java/lang/Exception"))
            .up()
            .xml()
            .toString();
        MatcherAssert.assertThat(
            String.format(
                "Exception table wasn't parsed correctly, please, check the resulting XMIR: \n%s\n",
                xml
            ),
            xml,
            new HasMethod("bar")
                .inside("Foo")
                .withTryCatch("java/lang/Exception")
        );
    }

    @Test
    void doesNotContainTryCatchBlock() {
        MatcherAssert.assertThat(
            "We expect that method without try-catch block doesn't contain try-catch directives.",
            new BytecodeClass().helloWorldMethod().xml().toString(),
            Matchers.not(Matchers.containsString("trycatchblocks"))
        );
    }

    @Test
    void visitsMultiArrayInstructionSuccessfully() throws ImpossibleModificationException {
        final DirectivesMethod method = new DirectivesMethod();
        new DirectivesMethodVisitor(method).visitMultiANewArrayInsn("java/lang/String", 2);
        MatcherAssert.assertThat(
            "MultiArray instruction wasn't visited successfully.",
            new Xembler(method).xml(),
            Matchers.allOf(
                Matchers.containsString("MULTIANEWARRAY"),
                Matchers.containsString("2")
            )
        );
    }

    @Test
    void visitsIisncInstructionSuccessfully() throws ImpossibleModificationException {
        final DirectivesMethod method = new DirectivesMethod();
        new DirectivesMethodVisitor(method).visitIincInsn(1, 2);
        MatcherAssert.assertThat(
            "Iinc instruction wasn't visited successfully.",
            new Xembler(method).xml(),
            Matchers.allOf(
                Matchers.containsString("IINC"),
                Matchers.containsString("1"),
                Matchers.containsString("2")
            )
        );
    }

    @Test
    void visitsLookupSwitchInstructionSuccessfully() throws ImpossibleModificationException {
        final DirectivesMethod method = new DirectivesMethod();
        new DirectivesMethodVisitor(method).visitLookupSwitchInsn(
            new Label(), new int[]{1, 2, 3}, new Label[]{new Label(), new Label(), new Label()}
        );
        MatcherAssert.assertThat(
            "LookupSwitch instruction wasn't visited successfully.",
            new Xembler(method).xml(),
            Matchers.allOf(
                Matchers.containsString("LOOKUPSWITCH"),
                Matchers.containsString("1"),
                Matchers.containsString("2"),
                Matchers.containsString("3"),
                Matchers.containsString("label")
            )
        );
    }

    @Test
    void visitsTableSwitchInstructionSuccessfully() throws ImpossibleModificationException {
        final DirectivesMethod method = new DirectivesMethod();
        new DirectivesMethodVisitor(method).visitTableSwitchInsn(
            1, 3, new Label(), new Label[]{new Label(), new Label(), new Label()}
        );
        MatcherAssert.assertThat(
            "TableSwitch instruction wasn't visited successfully.",
            new Xembler(method).xml(),
            Matchers.allOf(
                Matchers.containsString("TABLESWITCH"),
                Matchers.containsString("1"),
                Matchers.containsString("3"),
                Matchers.containsString("label")
            )
        );
    }
}
