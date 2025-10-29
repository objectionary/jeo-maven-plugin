/*
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
 * Test cases for {@link DirectivesMetas}.
 * This class verifies the generation of meta directives for classes,
 * including package information and class metadata.
 *
 * @since 0.1.0
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
        final String pckg = new AbsentPackage().toString();
        MatcherAssert.assertThat(
            "We expect that <metas>/<package> won't be created if package is empty",
            new Xembler(
                new DirectivesMetas(new ClassName("WithoutPackage"))
            ).xmlQuietly(),
            Matchers.allOf(
                XhtmlMatchers.hasXPath("/metas/meta/head[text()='package']"),
                XhtmlMatchers.hasXPath(String.format("/metas/meta/tail[text()='%s']", pckg)),
                XhtmlMatchers.hasXPath(String.format("/metas/meta/part[text()='%s']", pckg))
            )
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
