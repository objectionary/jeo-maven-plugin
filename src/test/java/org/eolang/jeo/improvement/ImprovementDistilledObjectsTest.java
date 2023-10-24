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
package org.eolang.jeo.improvement;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.eolang.jeo.Representation;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.directives.HasMethod;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link ImprovementDistilledObjects}.
 * @since 0.1.0
 * @todo #157:90min Implement more comprehensive tests for the optimization.
 *  The current test is very basic and doesn't cover all the cases. We need to add more tests
 *  to cover all the cases. Moreover, it makes sense to enforce combinesRepresentations() test.
 */
final class ImprovementDistilledObjectsTest {

    /**
     * Name of the combined class.
     */
    private static final String COMBINED = "org.eolang.jeo.A$B";

    @Test
    void appliesSuccessfully() {
        final Collection<? extends Representation> res = new ImprovementDistilledObjects().apply(
            Arrays.asList(
                ImprovementDistilledObjectsTest.classA(),
                ImprovementDistilledObjectsTest.classB()
            )
        );
        MatcherAssert.assertThat(
            "The number of IRs is not correct, after optimization we expect one extra IR",
            res.size(),
            Matchers.equalTo(3)
        );
        MatcherAssert.assertThat(
            "We expect the names of the generated IRs to be A, B and AB",
            res.stream().map(Representation::name).collect(Collectors.toList()),
            Matchers.hasItem(ImprovementDistilledObjectsTest.COMBINED)
        );
    }

    @Test
    void combinesRepresentations() {
        final Representation repr = new ImprovementDistilledObjects()
            .apply(
                Arrays.asList(
                    ImprovementDistilledObjectsTest.classA(),
                    ImprovementDistilledObjectsTest.classB()
                )
            ).stream()
            .filter(
                representation -> ImprovementDistilledObjectsTest.COMBINED
                    .equals(representation.name())
            )
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "Can't find representation for '%s'",
                        ImprovementDistilledObjectsTest.COMBINED
                    )
                )
            );
        final String combined = repr.toEO().toString();
        MatcherAssert.assertThat(
            String.format(
                "Can't find the constructor in new generated class. Here is the XMIR:%n%s%n",
                combined
            ),
            combined,
            new HasMethod("new").inside("org/eolang/jeo/A$B")
        );
    }

    /**
     * Generate a bytecode for the class A.
     * The class A is defined as:
     * <p>{@code
     * package org.eolang.jeo;
     * public class A {
     *     private int d;
     *     A(int d) {
     *         this.d = d;
     *     }
     *     int foo() {
     *         return d + 1;
     *     }
     * }
     * }</p>
     * @return Bytecode representation of the class A.
     * @todo #152:90min Try to compile the class A using the JavaCompiler API.
     *  The class A is defined in the method {@link #classA()}, but it is rather verbose.
     *  Instead, we can try to compile bytecode from Java code right inside test. It might simplify
     *  the code and make it more readable.
     */
    private static Representation classA() {
        return new BytecodeRepresentation(
            new BytecodeClass("org/eolang/jeo/A")
                .withField("d", "I", null, null, Opcodes.ACC_PRIVATE)
                .withMethod("<init>", "(I)V", Opcodes.ACC_PUBLIC)
                .instruction(Opcodes.ALOAD, 0)
                .instruction(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V")
                .instruction(Opcodes.ALOAD, 0)
                .instruction(Opcodes.ILOAD, 1)
                .instruction(Opcodes.PUTFIELD, "org/eolang/jeo/A", "d", "I")
                .instruction(Opcodes.RETURN)
                .up()
                .withMethod("foo", "()I", Opcodes.ACC_PUBLIC)
                .instruction(Opcodes.ALOAD, 0)
                .instruction(Opcodes.GETFIELD, "org/eolang/jeo/A", "d", "I")
                .instruction(Opcodes.ICONST_1)
                .instruction(Opcodes.IADD)
                .instruction(Opcodes.IRETURN)
                .up()
                .bytecode()
                .asBytes()
        );
    }

    /**
     * Generate a bytecode for the class B.
     * The class B is defined as:
     * <p>{@code
     * package org.eolang.jeo;
     * public final class B{
     *     private final A a;
     *     B(A a){
     *         this.a = a;
     *     }
     *     int bar(){
     *         return a.foo() + 2;
     *     }
     * }
     * }</p>
     * @return Bytecode representation of the class B.
     */
    private static Representation classB() {
        return new BytecodeRepresentation(
            new BytecodeClass("org/eolang/jeo/B")
                .withField(
                    "a",
                    "Lorg/eolang/jeo/A;",
                    null,
                    null,
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL
                )
                .withMethod("<init>", "(Lorg/eolang/jeo/A;)V", Opcodes.ACC_PUBLIC)
                .instruction(Opcodes.ALOAD, 0)
                .instruction(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V")
                .instruction(Opcodes.ALOAD, 0)
                .instruction(Opcodes.ALOAD, 1)
                .instruction(Opcodes.PUTFIELD, "org/eolang/jeo/B", "a", "Lorg/eolang/jeo/A;")
                .instruction(Opcodes.RETURN)
                .up()
                .withMethod("bar", "()I", Opcodes.ACC_PUBLIC)
                .instruction(Opcodes.ALOAD, 0)
                .instruction(Opcodes.GETFIELD, "org/eolang/jeo/B", "a", "Lorg/eolang/jeo/A;")
                .instruction(Opcodes.INVOKEVIRTUAL, "org/eolang/jeo/A", "foo", "()I")
                .instruction(Opcodes.ICONST_2)
                .instruction(Opcodes.IADD)
                .instruction(Opcodes.IRETURN)
                .up()
                .bytecode()
                .asBytes()
        );
    }
}
