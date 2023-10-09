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

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.xembly.Xembler;

/**
 * Test case for {@link org.eolang.jeo.representation.directives.DirectivesMethod}.
 * We create {@link org.eolang.jeo.representation.directives.DirectivesMethod} only in the context
 * of using {@link org.eolang.jeo.representation.directives.DirectivesClass} in other words,
 * {@link org.eolang.jeo.representation.directives.DirectivesMethod} can't be createad
 * without {@link org.eolang.jeo.representation.directives.DirectivesClass} and it is the main
 * reason why in all the test we create
 * {@link org.eolang.jeo.representation.directives.DirectivesClass}.
 *
 * @since 0.1.0
 */
class DirectivesMethodTest {

    @Test
    void parsesMethodInstructions() {
        final DirectivesClass visitor = new DirectivesClass();
        new ClassReader(
            new BytecodeClass()
                .withMethod("main")
                .descriptor("()I")
                .instruction(Opcodes.BIPUSH, 28)
                .instruction(Opcodes.IRETURN)
                .up()
                .bytecode()
                .asBytes()
        ).accept(visitor, 0);
        final XMLDocument document = new XMLDocument(new Xembler(visitor).xmlQuietly());
        MatcherAssert.assertThat(
            "Can't find a method in the final XML by using XPath",
            document,
            Matchers.allOf(
                XhtmlMatchers.hasXPath("/program/objects/o/o[contains(@name,'main')]"),
                XhtmlMatchers.hasXPath(
                    "/program/objects/o/o[contains(@name,'main')]/o[@base='seq']/o[@base='opcode' and contains(@name,'BIPUSH')]/o[@base='int' and @data='bytes' and text()='00 00 00 00 00 00 00 1C']"
                ),
                XhtmlMatchers.hasXPath(
                    "/program/objects/o/o[contains(@name,'main')]/o[@base='seq']/o[@base='opcode' and contains(@name,'IRETURN')]"
                )
            )
        );
    }

    /**
     * In this test we parse the next java code (but represented as bytecode).
     *
     * <p>
     *     {@code
     *
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
     *
     *     }
     * </p>
     */
    @Test
    void parsesMethodParameters() {
        final String clazz = "ParametersExample";
        final String method = "printSum";
        final String xml = new BytecodeClass(clazz)
            .withMethod("main", Opcodes.ACC_PUBLIC, Opcodes.ACC_STATIC)
            .descriptor("([Ljava/lang/String;)V")
            .instruction(Opcodes.NEW, clazz)
            .instruction(Opcodes.DUP)
            .instruction(Opcodes.INVOKESPECIAL, clazz, "<init>", "()V")
            .instruction(Opcodes.ASTORE, 1)
            .instruction(Opcodes.ALOAD, 1)
            .instruction(Opcodes.BIPUSH, 10)
            .instruction(Opcodes.BIPUSH, 20)
            .instruction(Opcodes.INVOKEVIRTUAL, clazz, method, "(II)V")
            .instruction(Opcodes.RETURN)
            .up()
            .withMethod(method, Opcodes.ACC_PUBLIC)
            .descriptor("(II)V")
            .instruction(Opcodes.ILOAD, 1)
            .instruction(Opcodes.ILOAD, 2)
            .instruction(Opcodes.IADD)
            .instruction(Opcodes.ISTORE, 3)
            .instruction(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .instruction(Opcodes.ILOAD, 3)
            .instruction(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V")
            .instruction(Opcodes.RETURN)
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
                .withParameter("arg__I__0")
                .withParameter("arg__I__1")
        );
    }

    @Test
    @Disabled("We have to implement constructor parsing first")
    void parsesConstructor() {
        Assertions.fail("We have to implement constructor parsing first");
    }

    @Test
    @Disabled("We have to implement constructor with parameters parsing first")
    void parsesConstructorWithParameters() {
        Assertions.fail("We have to implement constructor with parameters parsing first");
    }
}
