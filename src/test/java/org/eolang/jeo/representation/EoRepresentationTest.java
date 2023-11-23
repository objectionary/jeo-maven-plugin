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
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link EoRepresentation}.
 *
 * @since 0.1.0
 */
class EoRepresentationTest {

    /**
     * Message for assertion.
     */
    private static final String MESSAGE =
        "The bytecode representation of the EO object is not correct,%nexpected:%n%s%nbut got:%n%s";

    @Test
    void retrievesName() {
        final String expected = "org/eolang/foo/Math";
        final String actual = new EoRepresentation(new BytecodeClass(expected).xml()).name();
        MatcherAssert.assertThat(
            String.format(
                "The name of the class is not retrieved correctly, we expected '%s', but got '%s'",
                expected,
                actual
            ),
            actual,
            Matchers.equalTo(expected)
        );
    }

    @Test
    void returnsXmlRepresentationOfEo() {
        MatcherAssert.assertThat(
            "The XML representation of the EO object is not correct",
            new EoRepresentation(new BytecodeClass("org/eolang/foo/Math").xml()).toEO(),
            XhtmlMatchers.hasXPath("/program[@name='Math']")
        );
    }

    @Test
    void returnsBytecodeRepresentationOfEo() {
        final BytecodeClass clazz = new BytecodeClass("Bar");
        final Bytecode expected = clazz.bytecode();
        final Bytecode actual = new EoRepresentation(clazz.xml()).toBytecode();
        MatcherAssert.assertThat(
            String.format(EoRepresentationTest.MESSAGE, expected, actual),
            actual,
            Matchers.equalTo(expected)
        );
    }

    @Test
    void returnsBytecodeRepresentationOfEoObjectWithFields() {
        final Bytecode expected = new BytecodeClass("Fields")
            .withField("foo")
            .bytecode();
        final Bytecode actual = new EoRepresentation(
            new BytecodeRepresentation(expected).toEO()
        ).toBytecode();
        MatcherAssert.assertThat(
            String.format(EoRepresentationTest.MESSAGE, expected, actual),
            actual,
            Matchers.equalTo(expected)
        );
    }

    @Test
    void convertsHelloWordEoRepresentationIntoBytecode() {
        final String name = "org/eolang/jeo/Application";
        final Bytecode expected = new BytecodeClass(name)
            .helloWorldMethod()
            .bytecode();
        final Bytecode actual = new EoRepresentation(
            new BytecodeRepresentation(expected).toEO()
        ).toBytecode();
        MatcherAssert.assertThat(
            String.format(EoRepresentationTest.MESSAGE, expected, actual),
            actual,
            Matchers.equalTo(expected)
        );
    }
}
