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
import org.eolang.jeo.representation.ClassName;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
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

    @Test
    void createsDirectivesWithEmptyPackage() {
        MatcherAssert.assertThat(
            "We expect that <metas>/<package> won't be created if package is empty",
            new Xembler(new DirectivesMetas(new ClassName("WithoutPackage"))).xmlQuietly(),
            Matchers.not(XhtmlMatchers.hasXPath("/metas/meta[head[text()='package']]"))
        );
    }

    @Test
    void createsDirectivesWithVersion() {
        MatcherAssert.assertThat(
            "We expect that <metas>/<version> will be created",
            new Xembler(new DirectivesMetas(new ClassName("WithVersion"))).xmlQuietly(),
            XhtmlMatchers.hasXPath("/metas/meta[head[text()='version']]")
        );
    }
}
