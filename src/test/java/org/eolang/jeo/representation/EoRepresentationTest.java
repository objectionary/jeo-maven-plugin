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
import java.io.PrintWriter;
import java.io.StringWriter;
import org.eolang.jeo.representation.asm.BytecodeClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

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
        byte[] expected = new BytecodeClass("Bar")
            .withMethod("main")
            .up()
            .bytes();

        ClassReader classReader = new ClassReader(expected);

        final StringWriter out = new StringWriter();
        classReader.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(out)), 0);
        final String res = out.toString();

        final byte[] actual = new EoRepresentation(new Eo("Bar")).toBytecode();
        ClassReader otherReader = new ClassReader(actual);
        final StringWriter actualOut = new StringWriter();
        otherReader.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(actualOut)),
            0
        );
        final String actualRes = actualOut.toString();


        MatcherAssert.assertThat(
            String.format(
                "The bytecode representation of the EO object is not correct,%nexpected:%n%s%nbut got:%n%s",
                res,
                actualRes
            ),
            actual,
            Matchers.equalTo(expected)
        );
    }
}
