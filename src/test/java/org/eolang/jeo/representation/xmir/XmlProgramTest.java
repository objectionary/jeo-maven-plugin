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
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.matchers.SameXml;
import org.eolang.jeo.representation.ClassName;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XmlProgram}.
 *
 * @since 0.1
 */
final class XmlProgramTest {

    @Test
    void retrievesTopClass() {
        final String bar = "Bar";
        final XmlProgram program = new XmlProgram(new ClassName(bar));
        final XmlClass actual = program.top();
        final XmlClass expected = new XmlClass(bar);
        MatcherAssert.assertThat(
            String.format(
                "Can't retrieve top-level class from program %s. Expected %s%n Actual %s%n",
                program,
                expected,
                actual
            ),
            actual.toXml().toString(),
            new SameXml(expected.toXml())
        );
    }

    @Test
    void retrievesPackage() {
        final String expected = "com.matrix.foobar";
        final String actual = new XmlProgram(new ClassName(expected, "FooBar")).pckg();
        MatcherAssert.assertThat(
            String.format(
                "Can't retrieve package from program %s. Expected %s%n Actual %s%n",
                expected,
                expected,
                actual
            ),
            actual,
            Matchers.equalTo(expected)
        );
    }

    @Test
    void convertsToXml() {
        final XML expected = new XMLDocument(
            String.join(
                "",
                "<o name='TwoWayProgram'>",
                "<o name='foo'>",
                "</o></o>"
            )
        );
        MatcherAssert.assertThat(
            "Can't convert program to XML",
            new XmlProgram(expected).toXml(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void replacesTopClass() {
        final XmlProgram program = new XmlProgram(new ClassName("Bar"));
        final XmlClass expected = new XmlClass("Foo");
        MatcherAssert.assertThat(
            String.format(
                "Can't replace top-level class in program %s. Expected %s%n Actual %s%n",
                program,
                expected,
                program.top()
            ),
            program.replaceTopClass(expected).top(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void removesTopClass() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new XmlProgram(new ClassName("Bar")).withoutTopClass().top(),
            "Can't remove top-level class from program"
        );
    }
}
