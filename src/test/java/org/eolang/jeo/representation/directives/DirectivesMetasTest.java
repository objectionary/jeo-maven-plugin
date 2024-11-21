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
import org.eolang.jeo.representation.ClassName;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeProgram;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesMetas}.
 * @since 0.1
 */
final class DirectivesMetasTest {

    @Test
    void createsMetasWithPackage() {
        MatcherAssert.assertThat(
            "Can't create corresponding xembly directives for metas with package, head and tail for class package",
            new Xembler(
                new DirectivesMetas(new ClassName("path/to/SomeClass")),
                new Transformers.Node()
            ).xmlQuietly(),
            Matchers.allOf(
                XhtmlMatchers.hasXPath("/metas/meta/head[text()='package']"),
                XhtmlMatchers.hasXPath("/metas/meta/tail[text()='j$path.j$to']"),
                XhtmlMatchers.hasXPath("/metas/meta/part[text()='j$path.j$to']")
            )
        );
    }

    @Test
    void addsAliasesForAllTheRequiredObjects() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create corresponding xembly directives for all the required objects",
            new Xembler(
                new BytecodeProgram(new BytecodeClass().helloWorldMethod()).directives("")
            ).xml(),
            Matchers.allOf(
                XhtmlMatchers.hasXPath("/program/metas/meta/head[text()='alias']"),
                XhtmlMatchers.hasXPath("/program/metas/meta/tail[text()='jeo.opcode']"),
                XhtmlMatchers.hasXPath("/program/metas/meta/tail[text()='jeo.label']"),
                XhtmlMatchers.hasXPath("/program/metas/meta/part[text()='jeo.int']"),
                XhtmlMatchers.hasXPath("/program/metas/meta/part[text()='jeo.string']"),
                XhtmlMatchers.hasXPath("/program/metas/meta/part[text()='jeo.bool']"),
                XhtmlMatchers.hasXPath("/program/metas/meta/part[text()='jeo.param']"),
                XhtmlMatchers.hasXPath("/program/metas/meta/part[text()='jeo.params']")
            )
        );
    }

    @Test
    void addsNothingExceptPackage() {
        MatcherAssert.assertThat(
            "Can't create corresponding xembly directives for metas with package only",
            new Xembler(
                new DirectivesMetas(new ClassName("path/to/SomeClass")),
                new Transformers.Node()
            ).xmlQuietly(),
            Matchers.allOf(
                XhtmlMatchers.hasXPath("/metas/meta/head[text()='package']"),
                XhtmlMatchers.hasXPath("/metas/meta/tail[text()='j$path.j$to']"),
                XhtmlMatchers.hasXPath("/metas/meta/part[text()='j$path.j$to']"),
                Matchers.not(
                    XhtmlMatchers.hasXPath("/metas/meta/tail[text()='org.eolang.jeo.label']")
                ),
                Matchers.not(
                    XhtmlMatchers.hasXPath("/metas/meta/tail[text()='org.eolang.jeo.opcode']")
                )
            )
        );
    }
}
