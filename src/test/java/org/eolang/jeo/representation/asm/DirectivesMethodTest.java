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
package org.eolang.jeo.representation.asm;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.asm.generation.BytecodeClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesMethod}.
 * We create {@link DirectivesMethod} only in the context
 * of using {@link DirectivesClass} in other words, {@link DirectivesMethod} can't be createad
 * without {@link DirectivesClass} and it is the main reason why in all the test we create
 * {@link DirectivesClass}.
 *
 * @since 0.1.0
 * @todo #97:60min Add more user-friendly Hamcrest matchers.
 *  Right now, we have tests with complex XPath strings that check
 *  the resulting XML, which are hard to read and understand. I believe
 *  we need to simplify these checks. Perhaps, we should introduce a new
 *  Hamcrest matcher. Once completed, remove this puzzle.
 * @todo #97:60min Implement passing parameters to methods.
 *  Right now, we parse methods without parameters. We need to implement
 *  parsing of methods with parameters. Once completed, remove this puzzle.
 *  Don't forget to add unit tests for this feature.
 *  See {@link DirectivesMethodTest#parsesMethodParameters()}.
 */
class DirectivesMethodTest {

    @Test
    void parsesMethodInstructions() {
        final DirectivesClass visitor = new DirectivesClass();
        new ClassReader(
            new BytecodeClass()
                .withMethod("main")
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
     * In this test we parse the next java code (but represented as bytecode.)
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
        final XML xml = new BytecodeClass("ParametersExample").xml();

        System.out.println(xml);
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
