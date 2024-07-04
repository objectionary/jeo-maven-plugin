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
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.xmir.XmlClass;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesClass}.
 * @since 0.1
 */
final class DirectivesClassTest {

    @Test
    void createsWithSimpleConstructor() {
        MatcherAssert.assertThat(
            "Can't create class with simple constructor",
            new XMLDocument(
                new Xembler(
                    new DirectivesClass(new ClassName("Neo"), new DirectivesClassProperties()),
                    new Transformers.Node()
                ).xmlQuietly()
            ),
            Matchers.equalTo(
                new XMLDocument(
                    String.join(
                        "",
                        "<o abstract='' name='Neo'>",
                        "<o base=\"int\" data=\"bytes\" name=\"version\">00 00 00 00 00 00 00 34</o>\n",
                        "<o base='int' data='bytes' name='access'>00 00 00 00 00 00 00 00</o>",
                        "<o base='string' data='bytes' name='signature'/>",
                        "<o base='string' data='bytes' name='supername'/>",
                        "<o base='tuple' name='interfaces' star=''/>",
                        "</o>"
                    )
                )
            )
        );
    }

    @Test
    void appendsField() {
        final String xml = new Xembler(
            new DirectivesClass(new ClassName("Neo")).field(new DirectivesField()),
            new Transformers.Node()
        ).xmlQuietly();
        MatcherAssert.assertThat(
            String.format(
                "Can't append field to the class; result is: '%s'",
                new XMLDocument(xml)
            ),
            xml,
            XhtmlMatchers.hasXPath("/o[@name='Neo']/o[@base='field']")
        );
    }

    @Test
    void appendsMethod() {
        final String xml = new Xembler(
            new DirectivesClass(new ClassName("Neo")).method(new DirectivesMethod("method")),
            new Transformers.Node()
        ).xmlQuietly();
        MatcherAssert.assertThat(
            String.format(
                "Can't append method to the class, or the method is appended wrongly; result is: '%s'",
                new XMLDocument(xml)
            ),
            xml,
            XhtmlMatchers.hasXPath("/o[@name='Neo']/o[@name='method']")
        );
    }

    @Test
    void correctlyConvertsToDirectives() throws ImpossibleModificationException {
        final String name = "Foo";
        final int access = 100;
        final String signature = "java/lang/Object";
        final String supername = "java/lang/Runnable";
        final String interfce = "java/lang/Cloneable";
        final XmlClass clazz = new XmlClass(
            new XmlNode(
                new Xembler(
                    new DirectivesClass(
                        name,
                        new DirectivesClassProperties(
                            access,
                            signature,
                            supername,
                            interfce
                        )
                    )
                ).xml()
            )
        );
        MatcherAssert.assertThat(
            "Class name is not equal to expected",
            clazz.name(),
            Matchers.equalTo(name)
        );
        MatcherAssert.assertThat(
            "Class access is not equal to expected",
            clazz.properties().access(),
            Matchers.equalTo(access)
        );
        MatcherAssert.assertThat(
            "Class signature is not equal to expected",
            clazz.properties().signature().get(),
            Matchers.equalTo(signature)
        );
        MatcherAssert.assertThat(
            "Class supername is not equal to expected",
            clazz.properties().supername(),
            Matchers.equalTo(supername)
        );
        MatcherAssert.assertThat(
            "Class interface is not equal to expected",
            clazz.properties().interfaces()[0],
            Matchers.equalTo(interfce)
        );

    }

}
