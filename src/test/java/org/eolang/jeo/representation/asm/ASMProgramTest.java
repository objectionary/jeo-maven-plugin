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

import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.eolang.jeo.representation.directives.DirectivesProgram;
import org.eolang.jeo.representation.xmir.XmlProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.xembly.Xembler;

/**
 * Test case for {@link ASMProgram}.
 * @since 0.6.
 */
final class ASMProgramTest {

    @Test
    void convertsToBytecode() {
        final Bytecode same = new BytecodeProgram()
            .replaceTopClass(new BytecodeClass().helloWorldMethod())
            .bytecode();
        MatcherAssert.assertThat(
            "We expect to receive the same bytecode",
            new ASMProgram(same.asBytes()).bytecode().bytecode(),
            Matchers.equalTo(same)
        );
    }

    @Test
    void parsesAnnotations() throws Exception {
        final byte[] original = new BytesOf(new ResourceOf("FixedWidth.class")).asBytes();
        final Bytecode expected = new Bytecode(original);
        final Bytecode actual = new ASMProgram(original).bytecode().bytecode();
        MatcherAssert.assertThat(
            "We expect to receive the same bytecode",
            actual,
            Matchers.equalTo(expected)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"FixedWidth.class", "ParamAnnotation.class"})
    void convertsToBytecodeThenToXmirAndThenBackToBytecode(final String resource) throws Exception {
        final byte[] original = new BytesOf(new ResourceOf(resource)).asBytes();
        final BytecodeProgram bytecode = new ASMProgram(original).bytecode();
        final Bytecode expected = bytecode.bytecode();
        final BytecodeProgram actual = new XmlProgram(
            new Xembler(bytecode.directives("")).xml()
        ).bytecode();
        MatcherAssert.assertThat(
            "We expect to receive the same bytecode",
            actual.bytecode(),
            Matchers.equalTo(expected)
        );
    }


    @ParameterizedTest
    @Disabled
    @ValueSource(strings = "AnnotationsApplication.class")
    void disableVerification(final String resource) throws Exception {
        final byte[] original = new BytesOf(new ResourceOf(resource)).asBytes();
        final BytecodeProgram bytecode = new ASMProgram(original).bytecode();
        final BytecodeProgram expected = bytecode;
        final DirectivesProgram directives = bytecode.directives("");
        final String xml = new Xembler(directives).xml();
        System.out.println(xml);
        final BytecodeProgram actual = new XmlProgram(xml).bytecode();
        System.out.println("_____________________");
        System.out.println(expected);
        System.out.println("_____________________");
        System.out.println(actual);
        System.out.println("_____________________");
        MatcherAssert.assertThat(
            String.format(
                "We expect to receive the same bytecode."
            ),
            actual.bytecode(false),
            Matchers.equalTo(expected.bytecode(false))
        );
    }

}