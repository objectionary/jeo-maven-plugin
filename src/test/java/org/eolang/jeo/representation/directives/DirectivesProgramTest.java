/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesProgram}.
 * @since 0.1
 */
final class DirectivesProgramTest {

    @Test
    void createsCorrectDirectives() {
        final ClassName name = new ClassName("Foo");
        final String actual = new Xembler(
            new DirectivesProgram(new DirectivesClass(name), new DirectivesMetas(name)),
            new Transformers.Node()
        ).xmlQuietly();
        MatcherAssert.assertThat(
            String.format(
                "Can't correctly and without errors create program directives, received XML: %n%s",
                new XMLDocument(actual)
            ),
            actual,
            XhtmlMatchers.hasXPath("/program/objects/o[@name='Foo']")
        );
    }

    @Test
    void setsVersion() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that version and revision number will be set from MANIFEST.MF file",
            new Xembler(
                new BytecodeProgram(
                    new BytecodeClass("Some")
                ).directives("")
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/program[@version='1.2.3']",
                "/program[@revision='1234567']",
                "/program[@dob='2023-03-19T00:00:00']"
            )
        );
    }

    @Test
    void setsMilliseconds() throws ImpossibleModificationException {
        final ClassName clazz = new ClassName("Some");
        final DirectivesProgram program = new DirectivesProgram(
            "some code",
            10,
            new DirectivesClass(clazz),
            new DirectivesMetas(clazz)
        );
        MatcherAssert.assertThat(
            "We expect that milliseconds will be set",
            new Xembler(program).xml(),
            XhtmlMatchers.hasXPath("/program[@ms='10']")
        );
    }
}
