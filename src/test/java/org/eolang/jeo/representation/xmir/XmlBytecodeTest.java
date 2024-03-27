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
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.DirectivesMethod;
import org.eolang.jeo.representation.directives.DirectivesMethodProperties;
import org.eolang.jeo.representation.directives.DirectivesProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlBytecode}.
 *
 * @since 0.1
 */
final class XmlBytecodeTest {

    @Test
    void convertsGenericsMethodIntoBytecode() {
        MatcherAssert.assertThat(
            "Can't convert generics method into bytecode",
            new XmlBytecode(XmlBytecodeTest.classWithGenericMethod()).bytecode(),
            Matchers.notNullValue()
        );
    }

    @Test
    void convertsMethodWithExceptionIntoBytecode() {
        MatcherAssert.assertThat(
            "Can't convert method with exception into bytecode",
            new XmlBytecode(XmlBytecodeTest.classWithException()).bytecode(),
            Matchers.notNullValue()
        );
    }

    /**
     * Creates XML with class that contains generic method.
     * The XML representation of the following java class:
     * {@code
     *
     * package org.eolang.jeo.takes;
     *
     * public class StrangeClass {
     *   public static <T> void printElement(T element) {
     *     System.out.println(element);
     *   }
     * }
     *
     * }
     * @return XML representation of the class.
     */
    private static String classWithGenericMethod() {
        final ClassName name = new ClassName("org.eolang.jeo.takes", "StrangeClass");
        return new Xembler(
            new DirectivesProgram()
                .withClass(
                    name,
                    new DirectivesClass(name).method(
                        new DirectivesMethod(
                            "printElement",
                            new DirectivesMethodProperties(
                                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                                "(Ljava/lang/Object;)V",
                                "<T:Ljava/lang/Object;>(TT;)V"
                            )
                        )
                            .opcode(
                                Opcodes.GETSTATIC,
                                "java/lang/System",
                                "out",
                                "Ljava/io/PrintStream;",
                                false
                            )
                            .opcode(Opcodes.ALOAD, 0)
                            .opcode(
                                Opcodes.INVOKEVIRTUAL,
                                "java/io/PrintStream",
                                "println",
                                "(Ljava/lang/Object;)V",
                                false
                            )
                            .opcode(Opcodes.RETURN)
                    )
                )
        ).xmlQuietly();
    }

    /**
     * Creates XML with class that contains method that declares exception.
     * The XML representation of the following java class:
     * {@code
     *
     * public class Foo {
     *   public void bar() throws Exception {
     *     throw new Exception();
     *   }
     * }
     *
     * }
     *
     * @return XML representation of the class.
     */
    private static String classWithException() {
        final ClassName name = new ClassName("Foo");
        return new Xembler(
            new DirectivesProgram()
                .withClass(
                    name,
                    new DirectivesClass(name)
                        .method(
                            new DirectivesMethod(
                                "bar",
                                new DirectivesMethodProperties(
                                    Opcodes.ACC_PUBLIC,
                                    "()V",
                                    null,
                                    "java/lang/Exception"
                                )
                            )
                                .opcode(
                                    Opcodes.NEW,
                                    "java/lang/Exception"
                                )
                                .opcode(Opcodes.DUP)
                                .opcode(
                                    Opcodes.INVOKESPECIAL,
                                    "java/lang/Exception",
                                    "<init>",
                                    "()V",
                                    false
                                )
                                .opcode(Opcodes.ATHROW)
                        )
                )
        ).xmlQuietly();
    }
}
