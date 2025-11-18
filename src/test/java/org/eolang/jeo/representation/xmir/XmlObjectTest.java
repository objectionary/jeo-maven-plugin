/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeObject;
import org.eolang.jeo.representation.directives.DirectivesClass;
import org.eolang.jeo.representation.directives.DirectivesMetas;
import org.eolang.jeo.representation.directives.DirectivesMethod;
import org.eolang.jeo.representation.directives.DirectivesMethodProperties;
import org.eolang.jeo.representation.directives.DirectivesObject;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlObject}.
 * @since 0.1
 */
final class XmlObjectTest {

    @Test
    void convertsToBytecode() {
        final String pckg = "com.matrix.foobar";
        final String name = "FooBar";
        MatcherAssert.assertThat(
            "Can't convert program to bytecode.",
            new XmlObject(
                new XMLDocument(
                    new Xembler(
                        new DirectivesObject(
                            new DirectivesClass(new ClassName(pckg, name)),
                            new DirectivesMetas(
                                new ClassName(pckg, name)
                            )
                        )
                    ).xmlQuietly()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeObject(
                    pckg,
                    new BytecodeClass(String.format("%s.%s", pckg, name), 0)
                )
            )
        );
    }

    @Test
    void convertsGenericsMethodIntoBytecode() {
        MatcherAssert.assertThat(
            "Can't convert generics method into bytecode",
            new XmlObject(XmlObjectTest.classWithGenericMethod()).bytecode().bytecode(),
            Matchers.notNullValue()
        );
    }

    @Test
    void convertsMethodWithExceptionIntoBytecode() {
        MatcherAssert.assertThat(
            "Can't convert method with exception into bytecode",
            new XmlObject(XmlObjectTest.classWithException()).bytecode().bytecode(),
            Matchers.notNullValue()
        );
    }

    @Test
    void catchesProgramParsingException() {
        MatcherAssert.assertThat(
            "Exception message doesn't contain the expected text",
            Assertions.assertThrows(
                ParsingException.class,
                () -> new XmlObject(
                    new Xembler(
                        new Directives(
                            new BytecodeObject(
                                "com.example", new BytecodeClass("com.example.Foo", 0)
                            ).directives(new Format())
                        ).xpath(".//o").remove()
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
        final ClassName name = new ClassName("jeo.takes", "StrangeClass");
        return new Xembler(
            new DirectivesObject(
                new DirectivesClass(
                    name,
                    new DirectivesMethod(
                        "printElement",
                        new DirectivesMethodProperties(
                            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                            name.full(),
                            "(Ljava/lang/Object;)V",
                            "<T:Ljava/lang/Object;>(TT;)V"
                        )
                    )
                        .withOpcode(
                            0,
                            Opcodes.GETSTATIC,
                            "java/lang/System",
                            "out",
                            "Ljava/io/PrintStream;",
                            false
                        )
                        .withOpcode(1, Opcodes.ALOAD, 0)
                        .withOpcode(
                            2,
                            Opcodes.INVOKEVIRTUAL,
                            "java/io/PrintStream",
                            "println",
                            "(Ljava/lang/Object;)V",
                            false
                        )
                        .withOpcode(3, Opcodes.RETURN)
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
        final String method = "bar";
        return new Xembler(
            new DirectivesObject(
                new DirectivesClass(
                    name,
                    new DirectivesMethod(
                        method,
                        new DirectivesMethodProperties(
                            Opcodes.ACC_PUBLIC,
                            method,
                            "()V",
                            null,
                            "java/lang/Exception"
                        )
                    )
                        .withOpcode(
                            0,
                            Opcodes.NEW,
                            "java/lang/Exception"
                        )
                        .withOpcode(1, Opcodes.DUP)
                        .withOpcode(
                            2,
                            Opcodes.INVOKESPECIAL,
                            "java/lang/Exception",
                            "<init>",
                            "()V",
                            false
                        )
                        .withOpcode(3, Opcodes.ATHROW)
                ), new DirectivesMetas(name)
            )
        ).xmlQuietly();
    }
}
