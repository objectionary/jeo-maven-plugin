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
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XmlClass}.
 *
 * @since 0.1
 */
final class XmlClassTest {

    @Test
    void createsByName() {
        final String expected = "FooClass";
        final XmlClass klass = new XmlClass(expected);
        MatcherAssert.assertThat(
            String.format(
                "%s should create a class with name %s%n",
                klass,
                expected
            ),
            klass.name(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void retrievesConstructor() {
        final XML doc = new XMLDocument(
            String.join(
                "",
                "<o name='FooClass'>",
                "<o name='new'>",
                "</o></o>"
            )
        );
        MatcherAssert.assertThat(
            String.format(
                "%s should retrieve exactly one constructor from %n%s%n",
                XmlClass.class.getSimpleName(),
                doc
            ),
            new XmlClass(doc.node().getFirstChild()).constructors(),
            Matchers.hasSize(1)
        );
    }

    @Test
    void doesNotRetrieveConstructor() {
        final XML doc = new XMLDocument(
            String.join(
                "",
                "<o name='BarClass'>",
                "<o name='bar'>",
                "</o></o>"
            )
        );
        MatcherAssert.assertThat(
            String.format(
                "%s should not retrieve any constructor from %n%s%n",
                XmlClass.class.getSimpleName(),
                doc
            ),
            new XmlClass(doc).constructors(),
            Matchers.empty()
        );
    }

    @Test
    void addsMethods() {
        MatcherAssert.assertThat(
            "Methods should be added.",
            new XmlClass("Empty")
                .withMethods(new XmlMethod())
                .withMethods(new XmlMethod())
                .methods(),
            Matchers.hasSize(2)
        );
    }

    @Test
    void replacesMethods() {
        final XmlMethod second = new XmlMethod("two");
        final XmlMethod third = new XmlMethod("three");
        final List<XmlMethod> methods = new XmlClass("Replaced")
            .withMethods(new XmlMethod("one"))
            .replaceMethods(second, third).methods();
        MatcherAssert.assertThat(
            "Old methods should be removed",
            methods,
            Matchers.hasSize(2)
        );
        MatcherAssert.assertThat(
            "Methods should be replaced with new",
            methods,
            Matchers.containsInAnyOrder(second, third)
        );
    }

    @Test
    void cleansAllMethods() {
        MatcherAssert.assertThat(
            "Methods should be empty.",
            new XmlClass(
                "<o name='MethodClass'>",
                "<o name='bar'>",
                "</o></o>"
            ).withoutMethods().methods(),
            Matchers.empty()
        );
    }

    @Test
    void convertesXmlClassBackToXmlNode() {
        final XML expected = new XMLDocument(
            String.join(
                "",
                "<o name='TwoWay'>",
                "<o name='foo'>",
                "</o></o>"
            )
        );
        MatcherAssert.assertThat(
            "XmlClass should be correctly converted back to XML",
            new XmlClass(expected.node()).toXml().toString(),
            Matchers.equalTo(expected.toString())
        );
    }
}
