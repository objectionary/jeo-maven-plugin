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
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.matchers.SameXml;
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeClassProperties;
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
    void createsWithSimpleConstructor() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create class with simple constructor",
            new Xembler(
                new DirectivesClass(new ClassName("Neo"), new DirectivesClassProperties()),
                new Transformers.Node()
            ).xml(),
            new SameXml(
                String.join(
                    "",
                    "<o abstract='' name='Neo'>",
                    "<o base='org.eolang.jeo.int' data='bytes' name='version'>00 00 00 00 00 00 00 34</o>",
                    "<o base='org.eolang.jeo.int' data='bytes' name='access'>00 00 00 00 00 00 00 00</o>",
                    "<o base='org.eolang.jeo.string' data='bytes' name='signature'/>",
                    "<o base='org.eolang.jeo.string' data='bytes' name='supername'/>",
                    "<o base='org.eolang.seq0' name='interfaces'/>",
                    "<o base='org.eolang.seq0' name='annotations'/>",
                    "<o base='org.eolang.seq0' name='attributes'/>",
                    "</o>"
                )
            )
        );
    }

    @Test
    void appendsField() {
        final String xml = new Xembler(
            new DirectivesClass(new ClassName("Neo")).withField(new DirectivesField()),
            new Transformers.Node()
        ).xmlQuietly();
        MatcherAssert.assertThat(
            String.format(
                "Can't append field to the class; result is: '%s'",
                new XMLDocument(xml)
            ),
            xml,
            XhtmlMatchers.hasXPath("/o[@name='Neo']/o[contains(@base,'field')]")
        );
    }

    @Test
    void appendsMethod() {
        final String xml = new Xembler(
            new DirectivesClass(new ClassName("Neo")).withMethod(new DirectivesMethod("method")),
            new Transformers.Node()
        ).xmlQuietly();
        MatcherAssert.assertThat(
            String.format(
                "Can't append method to the class, or the method is appended wrongly; result is: '%s'",
                new XMLDocument(xml)
            ),
            xml,
            XhtmlMatchers.hasXPath("/o[@name='Neo']/o[contains(@name,'method')]")
        );
    }

    @Test
    void convertsToDirectives() throws ImpossibleModificationException {
        final String name = "Foo";
        final int access = 100;
        final String signature = "java/lang/Object";
        final String supername = "java/lang/Runnable";
        final String interfce = "java/lang/Cloneable";
        MatcherAssert.assertThat(
            "We expect that class created from directives is equal to expected",
            new XmlClass(
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
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeClass(
                    name,
                    new BytecodeClassProperties(access, signature, supername, interfce)
                )
            )
        );
    }

}
