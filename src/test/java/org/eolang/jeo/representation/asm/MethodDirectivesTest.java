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
import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.xembly.Xembler;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case for {@link MethodDirectives}.
 * We create {@link MethodDirectives} only in the context
 * of using {@link ClassDirectives} in other words, {@link MethodDirectives} can't be createad
 * without {@link ClassDirectives} and it is the main reason why in all the test we create
 * {@link ClassDirectives}.
 *
 * @since 0.1.0
 */
class MethodDirectivesTest {

    @Test
    void parsesMethodInstructions() {
        final ClassDirectives visitor = new ClassDirectives();
        new ClassReader(
            new BytecodeClass()
                .withMethod("main")
                .instruction(Opcodes.BIPUSH, 28)
                .instruction(Opcodes.IRETURN)
                .up()
                .bytes()
        ).accept(visitor, 0);
        final XMLDocument document = new XMLDocument(new Xembler(visitor).xmlQuietly());
        MatcherAssert.assertThat(
            "Can't find a method in the final XML by using XPath",
            document,
            Matchers.allOf(
                XhtmlMatchers.hasXPath("/program/objects/o/o[@name='main']"),
                XhtmlMatchers.hasXPath(
                    "/program/objects/o/o[@name='main']/o[@base='seq']/o[@base='opcode' and @name='BIPUSH-16-1']/o[@base='int' and @data='bytes' and text()='00 00 00 00 00 00 00 1C']"
                ),
                XhtmlMatchers.hasXPath(
                    "/program/objects/o/o[@name='main']/o[@base='seq']/o[@base='opcode' and @name='IRETURN-172-2']"
                )
            )
        );
    }

    void parsesMethodParameters() {
        fail();
    }

    void parsesConstructor() {
        fail();
    }

    void parsesConstructorWithParameters() {
        fail();
    }

}