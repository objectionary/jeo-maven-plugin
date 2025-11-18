/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.NumberedName;
import org.eolang.jeo.representation.asm.AsmProgram;
import org.eolang.jeo.representation.directives.DirectivesAnnotations;
import org.eolang.jeo.representation.directives.DirectivesAttributes;
import org.eolang.jeo.representation.directives.DirectivesInstruction;
import org.eolang.jeo.representation.directives.DirectivesMethod;
import org.eolang.jeo.representation.directives.DirectivesMethodProperties;
import org.eolang.jeo.representation.directives.Format;
import org.eolang.jeo.representation.xmir.XmlObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
    void generatesDirectivesForMethodWithInstructions() {
        MatcherAssert.assertThat(
            "We expect that method with instructions will generate correct directives",
            new Xembler(
                new BytecodeMethod("main")
                    .opcode(Opcodes.BIPUSH, 28)
                    .opcode(Opcodes.IRETURN)
                    .directives()
            ).xmlQuietly(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesMethod(
                        new Format(),
                        new NumberedName(1, "main"),
                        new DirectivesMethodProperties(),
                        Arrays.asList(
                            new DirectivesInstruction(0, new Format(), Opcodes.BIPUSH, 28),
                            new DirectivesInstruction(1, new Format(), Opcodes.IRETURN)
                        ),
                        Collections.emptyList(),
                        new DirectivesAnnotations(),
                        Collections.emptyList(),
                        new DirectivesAttributes("local-variable-table", Collections.emptyList())
                    )
                ).xmlQuietly()
            )
        );
    }

    /**
     * In this test, we parse the next java code (but represented as bytecode).
     * <p>
     *     {@code
     *     public class ParamsExample {
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
        final Format format = new Format();
        MatcherAssert.assertThat(
            "We expect that the method with parameters generates correct directives",
            new Xembler(
                new BytecodeMethod(
                    new BytecodeMethodProperties(
                        Opcodes.ACC_PUBLIC, "printSum", "(II)V", "", new BytecodeMethodParameters()
                    )
                ).opcode(Opcodes.NEW, "ParametersExample")
                    .opcode(Opcodes.DUP)
                    .opcode(Opcodes.INVOKESPECIAL, "ParametersExample", "<init>", "()V", false)
                    .opcode(Opcodes.ASTORE, 1)
                    .opcode(Opcodes.ALOAD, 1)
                    .opcode(Opcodes.BIPUSH, 10)
                    .opcode(Opcodes.BIPUSH, 20)
                    .opcode(Opcodes.INVOKEVIRTUAL, "ParametersExample", "printSum", "(II)V", false)
                    .opcode(Opcodes.RETURN)
                    .directives()
            ).xmlQuietly(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesMethod(
                        new Format(),
                        new NumberedName(1, "printSum"),
                        new DirectivesMethodProperties(
                            Opcodes.ACC_PUBLIC, "(II)V", ""
                        ),
                        Arrays.asList(
                            new DirectivesInstruction(0, format, Opcodes.NEW, "ParametersExample"),
                            new DirectivesInstruction(1, format, Opcodes.DUP),
                            new DirectivesInstruction(
                                2,
                                format,
                                Opcodes.INVOKESPECIAL,
                                "ParametersExample",
                                "<init>",
                                "()V",
                                false
                            ),
                            new DirectivesInstruction(3, format, Opcodes.ASTORE, 1),
                            new DirectivesInstruction(4, format, Opcodes.ALOAD, 1),
                            new DirectivesInstruction(5, format, Opcodes.BIPUSH, 10),
                            new DirectivesInstruction(6, format, Opcodes.BIPUSH, 20),
                            new DirectivesInstruction(
                                7,
                                format,
                                Opcodes.INVOKEVIRTUAL,
                                "ParametersExample",
                                "printSum",
                                "(II)V",
                                false
                            ),
                            new DirectivesInstruction(8, format, Opcodes.RETURN)
                        ),
                        Collections.emptyList(),
                        new DirectivesAnnotations(),
                        Collections.emptyList(),
                        new DirectivesAttributes("local-variable-table", Collections.emptyList())
                    )
                ).xmlQuietly()
            )
        );
    }

    @Test
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

    /**
     * This test was added to mitigate the following issue:
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1251">issue #1251</a>.
     * @throws ImpossibleModificationException if modification is impossible, programmer mistake.
     */
    @Test
    void addsLocalVariableTableEvenIfItIsEmpty() throws ImpossibleModificationException {
        final String name = "emptyLocalVariableTable";
        final DirectivesMethod directives = new BytecodeMethod(name).directives();
        MatcherAssert.assertThat(
            "We expect that the local variable table will be added even if it is empty",
            new Xembler(directives).xml(),
            XhtmlMatchers.hasXPaths(
                String.format(
                    "./o[contains(@name,'%s')]/o[@name='local-variable-table']",
                    name
                )
            )
        );
    }

    @ParameterizedTest(name = "Computing maxs for method {1}, expected  {2}")
    @MethodSource("implementedMethods")
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
    @MethodSource("abstractMethods")
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
    @MethodSource("realMethods")
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

    /**
     * Provides implemented methods for testing.
     * These methods contain different number of local variables and stack elements.
     * Used in
     * {@link #computesMaxsCorrectlyForImplementedMethods(BytecodeMethod, String, BytecodeMaxs)}.
     * @return Stream of arguments.
     */
    static Stream<Arguments> implementedMethods() {
        return BytecodeMethodTest.methods("maxs/Maxs.java");
    }

    /**
     * Provides methods for testing.
     * These methods are abstract.
     * Used in
     * {@link #computesMaxsCorrectlyForImplementedMethods(BytecodeMethod, String, BytecodeMaxs)}.
     * @return Stream of arguments.
     */
    static Stream<Arguments> abstractMethods() {
        return BytecodeMethodTest.methods("maxs/MaxInterface.java");
    }

    /**
     * Provides methods for testing from real bytecode that is used in real projects.
     * Before that, we disassemble and assemble the compiled class.
     * @return Stream of arguments.
     */
    static Stream<Arguments> realMethods() {
        return Stream.of(
            "AbstractEndpoint.class",
            "FastHttpDateFormat.class",
            "ByteArrayClassLoader$ChildFirst$PrependingEnumeration.class"
        ).flatMap(BytecodeMethodTest::disassembleAssemble);
    }

    /**
     * Disassembles and assembles the given compiled class.
     * @param compiled Compiled class as a path to the resource.
     * @return Stream of methods.
     * @checkstyle IllegalCatchCheck (25 lines)
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    static Stream<Arguments> disassembleAssemble(final String compiled) {
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
    static Stream<Arguments> methods(final String clazz) {
        return new AsmProgram(
            new JavaSourceClass(clazz).compile().bytes()
        ).bytecode().top().methods().stream().map(
            method -> Arguments.of(method, method.name(), method.currentMaxs())
        );
    }
}
