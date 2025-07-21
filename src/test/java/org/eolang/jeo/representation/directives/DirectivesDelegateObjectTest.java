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
    /**
     * Base attribute.
     */
    private static final String BASE = "maxs";

    @Test
    void generatesValidXmlRepresentationWithBase() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect the delegate object to have the base attribute",
            new Xembler(
                new DirectivesDelegateObject(
                    DirectivesDelegateObjectTest.BASE,
                    new DirectivesValue(1)
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                String.format("o/o[@base='%s' and @name='@']", DirectivesDelegateObjectTest.BASE)
            )
        );
    }

    @Test
    void generatesValidXmlRepresentationWithBaseAndAs() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect the delegate object to have base and as attributes",
            new Xembler(
                new DirectivesDelegateObject(
                    DirectivesDelegateObjectTest.BASE,
                    "asValue",
                    new DirectivesValue(1)
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                String.format(
                    "o[@as='asValue']/o[@base='%s' and @name='@']",
                    DirectivesDelegateObjectTest.BASE
                )
            )
        );
    }

    @Test
    void generatesValidXmlRepresentationWithBaseAsAndName() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect the delegate object to have base, as, and name attributes",
            new Xembler(
                new DirectivesDelegateObject(
                    DirectivesDelegateObjectTest.BASE,
                    "as",
                    "name",
                    new DirectivesValue(1)
                )
            ).xml(),
            XhtmlMatchers.hasXPath(
                String.format(
                    "o[@as='as' and @name='name']/o[@base='%s' and @name='@']",
                    DirectivesDelegateObjectTest.BASE
                )
            )
        );
    }
}
