/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesDelegateObject}.
 * @since 0.12.0
 */
final class DirectivesDelegateObjectTest {

    @Test
    void generatesValidXmlRepresentationWithBase() throws ImpossibleModificationException {
        final String base = "with-base";
        MatcherAssert.assertThat(
            "We expect the delegate object to have the base attribute",
            new Xembler(
                new DirectivesDelegateObject(
                    base,
                    new DirectivesValue(1)
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                String.format("o/o[@base='%s' and @name='φ']", base)
            )
        );
    }

    @Test
    void generatesValidXmlRepresentationWithBaseAndAs() throws ImpossibleModificationException {
        final String base = "with-base-and-as";
        MatcherAssert.assertThat(
            "We expect the delegate object to have base and as attributes",
            new Xembler(
                new DirectivesDelegateObject(base, "asValue", new DirectivesValue(1))
            ).xml(),
            XhtmlMatchers.hasXPath(
                String.format("o[@as='asValue']/o[@base='%s' and @name='φ']", base)
            )
        );
    }

    @Test
    void generatesValidXmlRepresentationWithBaseAsAndName() throws ImpossibleModificationException {
        final String base = "with-base-as-and-name";
        MatcherAssert.assertThat(
            "We expect the delegate object to have base, as, and name attributes",
            new Xembler(
                new DirectivesDelegateObject(
                    base,
                    "as",
                    "name",
                    new DirectivesValue(1)
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                String.format("o[@as='as' and @name='name']/o[@base='%s' and @name='φ']", base)
            )
        );
    }
}
