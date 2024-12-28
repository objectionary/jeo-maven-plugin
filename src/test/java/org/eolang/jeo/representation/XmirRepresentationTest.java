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
package org.eolang.jeo.representation;

import com.jcabi.log.Logger;
import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link XmirRepresentation}.
 *
 * @since 0.1.0
 */
final class XmirRepresentationTest {

    /**
     * Message for assertion.
     */
    private static final String MESSAGE =
        "The bytecode representation of the EO object is not correct,%nexpected:%n%s%nbut got:%n%s";

    /**
     * Math class name.
     */
    private static final String MATH = "org/eolang/foo/Math";

    @Test
    void retrievesName() {
        final String name = "Math";
        final String expected = "j$org/j$eolang/j$foo/j$Math";
        final String actual = new XmirRepresentation(
            new BytecodeProgram(
                "org/eolang/foo",
                new BytecodeClass(name)
            ).xml()
        ).name();
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
            new BytecodeProgram(
                "org.eolang",
                new BytecodeClass(XmirRepresentationTest.MATH)
            ).xml(),
            XhtmlMatchers.hasXPath("/program[@name='j$Math']")
        );
    }

    @Test
    void returnsBytecodeRepresentationOfEo() {
        final String name = "Bar";
        final BytecodeClass clazz = new BytecodeClass(name);
        final Bytecode expected = new BytecodeProgram(
            new BytecodeClass(name)
        ).bytecode();
        final Bytecode actual = new XmirRepresentation(
            new BytecodeProgram(clazz).xml()
        ).toBytecode();
        MatcherAssert.assertThat(
            String.format(XmirRepresentationTest.MESSAGE, expected, actual),
            actual,
            Matchers.equalTo(expected)
        );
    }

    @Test
    void returnsBytecodeRepresentationOfEoObjectWithFields() {
        final Bytecode expected = new BytecodeProgram(
            new BytecodeClass("Fields").withField("foo")
        ).bytecode();
        final Bytecode actual = new XmirRepresentation(
            new BytecodeRepresentation(expected).toEO()
        ).toBytecode();
        MatcherAssert.assertThat(
            String.format(XmirRepresentationTest.MESSAGE, expected, actual),
            actual,
            Matchers.equalTo(expected)
        );
    }

    @Test
    void convertsHelloWordEoRepresentationIntoBytecode() {
        final Bytecode expected = new BytecodeProgram(
            new BytecodeClass("Application").helloWorldMethod()
        ).bytecode();
        final Bytecode actual = new XmirRepresentation(
            new BytecodeRepresentation(expected).toEO()
        ).toBytecode();
        MatcherAssert.assertThat(
            String.format(XmirRepresentationTest.MESSAGE, expected, actual),
            actual,
            Matchers.equalTo(expected)
        );
    }

    @Test
    void failsToOpenBrokenXmirRepresentationFromFile(@TempDir final Path dir) throws IOException {
        final Path xmir = dir.resolve("Math.xmir");
        Files.write(
            xmir,
            new BytecodeProgram(
                new BytecodeClass(XmirRepresentationTest.MATH)
            ).xml().toString().substring(42).getBytes(StandardCharsets.UTF_8)
        );
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new XmirRepresentation(xmir).toBytecode(),
            "We expect that the error message will be easily understandable by developers"
        );
    }

    /**
     * This is a performance test, which is disabled by default.
     * It is used to measure the performance of the conversion of the EO object
     * into the bytecode representation and back.
     */
    @Test
    @Disabled
    @SuppressWarnings("PMD.GuardLogStatement")
    void convertsToXmirAndBack() {
        final Bytecode before = new BytecodeProgram(
            new BytecodeClass(XmirRepresentationTest.MATH)
                .helloWorldMethod()
        ).bytecode();
        final int attempts = 500;
        final long start = System.currentTimeMillis();
        for (int current = 0; current < attempts; ++current) {
            final Bytecode actual = new XmirRepresentation(
                new BytecodeRepresentation(
                    before
                ).toEO()
            ).toBytecode();
            MatcherAssert.assertThat(
                String.format(XmirRepresentationTest.MESSAGE, before, actual),
                actual,
                Matchers.equalTo(before)
            );
        }
        final long end = System.currentTimeMillis();
        Logger.info(
            this,
            "We made %d attempts to convert bytecode to xmir and back in %[ms]s",
            attempts,
            end - start
        );
    }

    @Test
    void createsXmirRepresentationFromFile(@TempDir final Path path) throws IOException {
        final BytecodeProgram program = new BytecodeProgram(
            new BytecodeClass(XmirRepresentationTest.MATH).helloWorldMethod()
        );
        final Bytecode expected = program.bytecode();
        final Path address = path.resolve("Math.xmir");
        Files.write(address, program.xml().toString().getBytes(StandardCharsets.UTF_8));
        MatcherAssert.assertThat(
            "We expect that Xmir representation will be created from the file successfully",
            new XmirRepresentation(address).toBytecode(),
            Matchers.equalTo(expected)
        );
    }
}
