/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import it.JavaSourceClass;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.asm.AsmProgram;
import org.eolang.jeo.representation.directives.HasMethod;
import org.eolang.jeo.representation.xmir.XmlObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link BytecodeMethod}.
 * @since 0.6
 * @todo #1130 Remove {@link HasMethod} class and enable tests in {@link BytecodeMethodTest} class.
 *  We should find a way to test {@link BytecodeMethod#directives()} method without using
 *  {@link HasMethod} class. Once we find a way to do it, we should enable the following tests:
 *  - {@link #parsesMethodParameters}
 *  - {@link #parsesConstructor}
 *  - {@link #parsesConstructorWithParameters}
 *  - {@link #parsesIfStatementCorrectly}
 *  - {@link #parsesTryCatchInstructions}
 *  - {@link #doesNotContainTryCatchBlock}
 *  - {@link #generatesDirectivesForMethodWithInstructions()}
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
final class BytecodeMethodTest {

    @Test
    void clearsMaxs() {
        MatcherAssert.assertThat(
            "We expect that method without maxs is equal to method with cleaned maxs",
            new BytecodeMethod("foo", new BytecodeMaxs(1, 2)).withoutMaxs(),
            Matchers.equalTo(new BytecodeMethod("foo"))
        );
    }

    @Test
    @Disabled
    void generatesDirectivesForMethodWithInstructions() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new BytecodeObject(
                new BytecodeClass()
                    .withMethod(new BytecodeMethodProperties("main", "()I"))
                    .opcode(Opcodes.BIPUSH, 28)
                    .opcode(Opcodes.IRETURN)
                    .up()
            ).directives("")
        ).xml();
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
     * In this test, we parse the next java code (but represented as bytecode).
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
    @Disabled
    void parsesMethodParameters() {
        final String clazz = "ParametersExample";
        final String method = "printSum";
        final String xml = new BytecodeObject(
            new BytecodeClass(clazz)
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
        ).xml().toString();
        MatcherAssert.assertThat(
            String.format(
                "DirectivesMethod doesn't parse method parameters correctly. Please, check the resulting XMIR: \n%s\n",
                xml
            ),
            xml,
            new HasMethod(method)
                .inside(clazz)
                .withParameter("arg0")
                .withParameter("arg1")
        );
    }

    /**
     * In this test case, we parse the following java code.
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
    @Disabled
    void parsesConstructor() {
        final String xml = new BytecodeObject(
            new BytecodeClass("ConstructorExample")
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
        ).xml().toString();
        MatcherAssert.assertThat(
            String.format(
                "Constructor wasn't parsed correctly, please, check the resulting XMIR: \n%s\n",
                new XMLDocument(xml)
            ),
            xml,
            new HasMethod("@init@")
                .inside("ConstructorExample")
                .withInstruction(Opcodes.LDC, "Hello, constructor!")
        );
    }

    /**
     * In this test case, we parse the following java code.
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
    @Disabled
    void parsesConstructorWithParameters() {
        final String clazz = "ConstructorParams";
        final String xml = new BytecodeObject(
            new BytecodeClass(clazz)
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
        ).xml().toString();
        MatcherAssert.assertThat(
            String.format(
                "Constructor wasn't parsed correctly, please, check the resulting XMIR: \n%s\n",
                new XMLDocument(xml)
            ),
            xml,
            new HasMethod("@init@")
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
    @Disabled
    void parsesIfStatementCorrectly() {
        final String label = UUID.randomUUID().toString();
        final String xml = new BytecodeObject(
            new BytecodeClass("Foo")
                .withMethod("bar", "(D)I", 0)
                .opcode(Opcodes.DLOAD, 1)
                .opcode(Opcodes.DCONST_0)
                .opcode(Opcodes.DCMPL)
                .opcode(Opcodes.IFLE, new BytecodeLabel(label))
                .opcode(Opcodes.ICONST_5)
                .opcode(Opcodes.IRETURN)
                .label(label)
                .opcode(Opcodes.BIPUSH, 8)
                .opcode(Opcodes.IRETURN)
                .up()
        ).xml().toString();
        MatcherAssert.assertThat(
            String.format(
                "If statement wasn't parsed correctly, please, check the resulting XMIR: \n%s\n",
                xml
            ),
            xml,
            new HasMethod("bar")
                .inside("Foo")
                .withInstruction(Opcodes.IFLE, new BytecodeLabel(label))
                .withLabel()
        );
    }

    /**
     * In this test case, we parse the following java code.
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
    @Disabled
    void parsesTryCatchInstructions() {
        final String start = UUID.randomUUID().toString();
        final String end = UUID.randomUUID().toString();
        final String handler = UUID.randomUUID().toString();
        final String xml = new BytecodeObject(
            new BytecodeClass("Foo")
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
        ).xml().toString();
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
    @Disabled
    void doesNotContainTryCatchBlock() {
        MatcherAssert.assertThat(
            "We expect that method without try-catch block doesn't contain try-catch directives.",
            new BytecodeObject(new BytecodeClass().helloWorldMethod()).xml().toString(),
            XhtmlMatchers.hasXPath(
                ".//o[contains(@base,'seq.of0')]"
            )
        );
    }

    @Test
    void visitsMultiArrayInstructionSuccessfully() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "MultiArray instruction wasn't visited successfully.",
            new Xembler(
                new BytecodeMethod().entry(
                    new BytecodeInstruction(Opcodes.MULTIANEWARRAY, "java/lang/String", 2)
                ).directives()
            ).xml(),
            Matchers.allOf(
                Matchers.containsString("multianewarray"),
                Matchers.containsString("2")
            )
        );
    }

    @Test
    void visitsIisncInstructionSuccessfully() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Iinc instruction wasn't visited successfully.",
            new Xembler(
                new BytecodeMethod().entry(
                    new BytecodeInstruction(Opcodes.IINC, 1, 2)
                ).directives()
            ).xml(),
            Matchers.allOf(
                Matchers.containsString("iinc"),
                Matchers.containsString("1"),
                Matchers.containsString("2")
            )
        );
    }

    @Test
    void visitsLookupSwitchInstructionSuccessfully() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "LookupSwitch instruction wasn't visited successfully.",
            new Xembler(
                new BytecodeMethod().entry(
                    new BytecodeInstruction(
                        Opcodes.LOOKUPSWITCH,
                        Stream.concat(
                            Stream.of(new Label()),
                            Stream.concat(
                                Stream.of(1, 2, 3),
                                Stream.of(new Label(), new Label(), new Label())
                            )
                        ).toArray(Object[]::new)
                    )
                ).directives()
            ).xml(),
            Matchers.allOf(
                Matchers.containsString("lookupswitch"),
                Matchers.containsString("1"),
                Matchers.containsString("2"),
                Matchers.containsString("3"),
                Matchers.containsString("label")
            )
        );
    }

    @Test
    void visitsTableSwitchInstructionSuccessfully() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "TableSwitch instruction wasn't visited successfully.",
            new Xembler(
                new BytecodeMethod().entry(
                    new BytecodeInstruction(
                        Opcodes.TABLESWITCH,
                        Stream.concat(
                            Stream.of(1, 3, new Label()),
                            Stream.of(new Label(), new Label(), new Label())
                        ).toArray(Object[]::new)
                    )
                ).directives()
            ).xml(),
            Matchers.allOf(
                Matchers.containsString("tableswitch"),
                Matchers.containsString("1"),
                Matchers.containsString("3"),
                Matchers.containsString("label")
            )
        );
    }

    @ParameterizedTest(name = "Computing maxs for method {1}, expected  {2}")
    @MethodSource("implementedMaxs")
    void computesMaxsCorrectlyForImplementedMethods(
        final BytecodeMethod method,
        final String name,
        final BytecodeMaxs expected
    ) {
        MatcherAssert.assertThat(
            String.format(
                "Maxs weren't computed correctly for method %s",
                name
            ),
            method.computeMaxs(),
            Matchers.equalTo(expected)
        );
    }

    @ParameterizedTest(name = "Computing maxs for method {1}, expected  {2}")
    @MethodSource("abstractMaxs")
    void computesMaxsCorrectlyForAbstractMethods(
        final BytecodeMethod method,
        final String name,
        final BytecodeMaxs expected
    ) {
        MatcherAssert.assertThat(
            String.format(
                "Maxs weren't computed correctly for method %s",
                name
            ),
            method.computeMaxs(),
            Matchers.equalTo(expected)
        );
    }

    @ParameterizedTest(name = "Computing maxs for method {1}, expected  {2}")
    @MethodSource("realMaxs")
    void computesMaxForRealClassAfterAllTransformations(
        final BytecodeMethod method,
        final String name,
        final BytecodeMaxs expected
    ) {
        MatcherAssert.assertThat(
            String.format(
                "Maxs weren't computed correctly for real class method %s, with the following insturcitons: %n%s%n",
                name,
                method.instructionsView()
            ),
            method.computeMaxs(),
            Matchers.equalTo(expected)
        );
    }

    @ParameterizedTest
    @MethodSource("implementedFrames")
    void computesStackMapFramesForSomeMethods(
        final BytecodeMethod method,
        final String name,
        final List<BytecodeFrame> expected
    ) {
        MatcherAssert.assertThat(
            String.format(
                "Stack map frames weren't computed correctly for method '%s' with 'max_stack=%d' and 'max_locals=%d, with the following instructions: %n%s%n",
                name,
                method.currentMaxs().stack(),
                method.currentMaxs().locals(),
                method.instructionsView()
            ),
            method.computeFrames(),
            Matchers.equalTo(expected)
        );
    }

    /**
     * Provides implemented methods for testing.
     * These methods contain different stack map frames.
     * Used in {@link #computesStackMapFramesForSomeMethods(BytecodeMethod, String, List)}.
     * @return Stream of argumentsForTesting().
     */
    static Stream<Arguments> implementedFrames() {
        return BytecodeMethodTest.methodFrames("maxs/Maxs.java");
    }

    /**
     * Provides implemented methods for testing.
     * These methods contain different number of local variables and stack elements.
     * Used in
     * {@link #computesMaxsCorrectlyForImplementedMethods(BytecodeMethod, String, BytecodeMaxs)}.
     * @return Stream of arguments.
     */
    static Stream<Arguments> implementedMaxs() {
        return BytecodeMethodTest.methodMaxs("maxs/Maxs.java");
    }

    /**
     * Provides methods for testing.
     * These methods are abstract.
     * Used in
     * {@link #computesMaxsCorrectlyForImplementedMethods(BytecodeMethod, String, BytecodeMaxs)}.
     * @return Stream of arguments.
     */
    static Stream<Arguments> abstractMaxs() {
        return BytecodeMethodTest.methodMaxs("maxs/MaxInterface.java");
    }

    /**
     * Provides methods for testing from real bytecode that is used in real projects.
     * Before that, we disassemble and assemble the compiled class.
     * @return Stream of arguments.
     */
    static Stream<Arguments> realMaxs() {
        return Stream.of(
            "AbstractEndpoint.class",
            "FastHttpDateFormat.class",
            "ByteArrayClassLoader$ChildFirst$PrependingEnumeration.class"
        ).flatMap(BytecodeMethodTest::realMaxs);
    }

    /**
     * Disassembles and assembles the given compiled class.
     * @param compiled Compiled class as a path to the resource.
     * @return Stream of methods.
     * @checkstyle IllegalCatchCheck (25 lines)
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    static Stream<Arguments> realMaxs(final String compiled) {
        try {
            return new XmlObject(
                new BytecodeRepresentation(
                    new Bytecode(new BytesOf(new ResourceOf(compiled)).asBytes())
                ).toXmir()
            ).bytecode()
                .top()
                .methods().stream()
                .map(method -> Arguments.of(method, method.name(), method.currentMaxs()));
        } catch (final Exception ex) {
            throw new IllegalStateException(
                String.format(
                    "Can't disassemble and assemble the class %s",
                    compiled
                ),
                ex
            );
        }
    }

    /**
     * Provides methods for testing.
     * @param clazz Resource class name.
     * @return Stream of arguments.
     */
    static Stream<Arguments> methodMaxs(final String clazz) {
        return new AsmProgram(
            new JavaSourceClass(clazz).compile().bytes()
        ).bytecode().top().methods().stream().map(
            method -> Arguments.of(method, method.name(), method.currentMaxs())
        );
    }

    /**
     * Provides method frames for testing.
     * @param clazz Resource class name.
     * @return Stream of arguments.
     */
    static Stream<Arguments> methodFrames(final String clazz) {
        return new AsmProgram(
            new JavaSourceClass(clazz).compile().bytes()
        ).bytecode().top().methods().stream().map(
            method -> Arguments.of(method, method.name(), method.currentFrames())
        );
    }
}
