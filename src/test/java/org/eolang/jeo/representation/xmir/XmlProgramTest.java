/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.DirectivesMetas;
import org.eolang.jeo.representation.directives.DirectivesMethod;
import org.eolang.jeo.representation.directives.DirectivesMethodProperties;
import org.eolang.jeo.representation.directives.DirectivesProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlProgram}.
 * @since 0.1
 */
final class XmlProgramTest {

    @Test
    void convertsToBytecode() {
        final String pckg = "com.matrix.foobar";
        final String name = "FooBar";
        MatcherAssert.assertThat(
            "Can't convert program to bytecode.",
            new XmlProgram(new ClassName(pckg, name)).bytecode(),
            Matchers.equalTo(
                new BytecodeProgram(
                    pckg,
                    new BytecodeClass(name, 0)
                )
            )
        );
    }

    @Test
    void convertsGenericsMethodIntoBytecode() {
        MatcherAssert.assertThat(
            "Can't convert generics method into bytecode",
            new XmlProgram(XmlProgramTest.classWithGenericMethod()).bytecode().bytecode(),
            Matchers.notNullValue()
        );
    }

    @Test
    void convertsMethodWithExceptionIntoBytecode() {
        MatcherAssert.assertThat(
            "Can't convert method with exception into bytecode",
            new XmlProgram(XmlProgramTest.classWithException()).bytecode().bytecode(),
            Matchers.notNullValue()
        );
    }

    @Test
    void catchesProgramParsingException() {
        MatcherAssert.assertThat(
            "Exception message doesn't contain the expected text",
            Assertions.assertThrows(
                ParsingException.class,
                () -> new XmlProgram(
                    new Xembler(
                        new Directives(
                            new BytecodeProgram(
                                "com.example", new BytecodeClass("Foo", 0)
                            ).directives("")
                        ).xpath(".//objects").remove()
                    ).xmlQuietly()
                ).bytecode()
            ).getMessage(),
            Matchers.containsString(
                "Unexpected exception during parsing the program in package 'com.example'"
            )
        );
    }

    /**
     * Creates XML with class that contains generic method.
     * The XML representation of the following java class:
     * {@code
     * package org.eolang.jeo.takes;
     * public class StrangeClass {
     *   public static <T> void printElement(T element) {
     *     System.out.println(element);
     *   }
     * }
     * }
     * @return XML representation of the class.
     */
    private static String classWithGenericMethod() {
        final ClassName name = new ClassName("org.eolang.jeo.takes", "StrangeClass");
        return new Xembler(
            new DirectivesProgram(
                new DirectivesClass(
                    name,
                    new DirectivesMethod(
                        "printElement",
                        new DirectivesMethodProperties(
                            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                            "(Ljava/lang/Object;)V",
                            "<T:Ljava/lang/Object;>(TT;)V"
                        )
                    )
                        .withOpcode(
                            Opcodes.GETSTATIC,
                            "java/lang/System",
                            "out",
                            "Ljava/io/PrintStream;",
                            false
                        )
                        .withOpcode(Opcodes.ALOAD, 0)
                        .withOpcode(
                            Opcodes.INVOKEVIRTUAL,
                            "java/io/PrintStream",
                            "println",
                            "(Ljava/lang/Object;)V",
                            false
                        )
                        .withOpcode(Opcodes.RETURN)
                ), new DirectivesMetas(name)
            )
        ).xmlQuietly();
    }

    /**
     * Creates XML with class that contains method that declares exception.
     * The XML representation of the following java class:
     * {@code
     * public class Foo {
     *   public void bar() throws Exception {
     *     throw new Exception();
     *   }
     * }
     * }
     * @return XML representation of the class.
     */
    private static String classWithException() {
        final ClassName name = new ClassName("Foo");
        return new Xembler(
            new DirectivesProgram(
                new DirectivesClass(
                    name,
                    new DirectivesMethod(
                        "bar",
                        new DirectivesMethodProperties(
                            Opcodes.ACC_PUBLIC,
                            "()V",
                            null,
                            "java/lang/Exception"
                        )
                    )
                        .withOpcode(
                            Opcodes.NEW,
                            "java/lang/Exception"
                        )
                        .withOpcode(Opcodes.DUP)
                        .withOpcode(
                            Opcodes.INVOKESPECIAL,
                            "java/lang/Exception",
                            "<init>",
                            "()V",
                            false
                        )
                        .withOpcode(Opcodes.ATHROW)
                ), new DirectivesMetas(name)
            )
        ).xmlQuietly();
    }
}
