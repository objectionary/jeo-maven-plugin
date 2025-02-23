/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
