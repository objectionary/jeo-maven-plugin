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
package org.eolang.jeo.representation;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import org.eolang.jeo.representation.asm.Bytecode;
import org.eolang.jeo.representation.asm.BytecodeClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Test case for {@link EoRepresentation}.
 *
 * @since 0.1.0
 */
class EoRepresentationTest {

    @Test
    void retrievesName() {
        final String expected = "org.eolang.foo.Math";
        final String actual = new EoRepresentation(new Eo(expected)).name();
        MatcherAssert.assertThat(
            String.format(
                "The name of the class is not retrieved correctly, we expected '%s', but got '%s'",
                expected,
                actual
            ),
            expected,
            Matchers.equalTo(actual)
        );
    }

    @Test
    void returnsXmlRepresentationOfEo() {
        MatcherAssert.assertThat(
            "The XML representation of the EO object is not correct",
            new EoRepresentation(new Eo("org.eolang.foo.Math")).toEO(),
            XhtmlMatchers.hasXPath("/program[@name='org.eolang.foo.Math']")
        );
    }

    @Test
    void returnsBytecodeRepresentationOfEo() {
        Bytecode expected = new BytecodeClass("Bar")
            .withMethod("main", Opcodes.ACC_PUBLIC, Opcodes.ACC_STATIC)
            .descriptor("([Ljava/lang/String;)V")
            .up()
            .bytecode();
        final Bytecode actual = new EoRepresentation(new Eo("Bar")).toBytecode();
        MatcherAssert.assertThat(
            String.format(
                "The bytecode representation of the EO object is not correct,%nexpected:%n%s%nbut got:%n%s",
                expected,
                actual
            ),
            actual,
            Matchers.equalTo(expected)
        );
    }


    @Test
    void convertsHelloWordEoRepresentationIntoBytecode() {
        final String name = "org/eolang/jeo/Application";
        final Bytecode expected = new BytecodeClass(name)
            .withMethod("main", Opcodes.ACC_PUBLIC, Opcodes.ACC_STATIC)
            .descriptor("([Ljava/lang/String;)V")
            .instruction(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            .instruction(Opcodes.LDC, "Hello, world!")
            .instruction(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V"
            )
            .up()
            .bytecode();
        final XML eo = new BytecodeRepresentation(expected.asBytes()).toEO();
        System.out.println(eo);
        final Bytecode actual = new EoRepresentation(
            eo
        ).toBytecode();
        MatcherAssert.assertThat(
            String.format(
                "The bytecode representation of the EO object is not correct,%nexpected:%n%s%nbut got:%n%s",
                expected,
                actual
            ),
            actual.asBytes(),
            Matchers.equalTo(expected.asBytes())
        );
    }
}
