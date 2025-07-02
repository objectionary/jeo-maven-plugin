/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Test for {@link DirectivesClosedObject}.
 * @since 0.11.0
 */
final class DirectivesClosedObjectTest {

    /**
     * Base attribute.
     */
    private static final String BASE = "base";

    @Test
    void createsClosedObjectWithBaseOnly() {
        MatcherAssert.assertThat(
            "We expect the closed object to have only the base attribute",
            new Xembler(
                new DirectivesClosedObject(DirectivesClosedObjectTest.BASE, Collections.emptyList())
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[@base='base' and not(@as) and not(@name)]")
        );
    }

    @Test
    void createsClosedObjectWithBaseAndAs() {
        MatcherAssert.assertThat(
            "We expect the closed object to have base and as attributes",
            new Xembler(
                new DirectivesClosedObject(
                    DirectivesClosedObjectTest.BASE, "asValue", Collections.emptyList()
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[@base='base' and @as='asValue' and not(@name)]")
        );
    }

    @Test
    void createsClosedObjectWithBaseAsAndName() {
        MatcherAssert.assertThat(
            "We expect the closed object to have base, as, and name attributes",
            new Xembler(
                new DirectivesClosedObject(
                    DirectivesClosedObjectTest.BASE,
                    "asValue",
                    "nameValue",
                    Collections.emptyList()
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[@base='base' and @as='asValue' and @name='nameValue']")
        );
    }

    @Test
    void createsClosedObjectWithInternalDirectives() {
        MatcherAssert.assertThat(
            "We expect the closed object to contain internal directives",
            new Xembler(
                new DirectivesClosedObject(
                    DirectivesClosedObjectTest.BASE,
                    new Directives().add("inner")
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[@base='base']/inner")
        );
    }

    @Test
    void doesNotAddEmptyAsOrNameAttributes() {
        MatcherAssert.assertThat(
            "We expect the closed object to not have as or name attributes when they are empty",
            new Xembler(
                new DirectivesClosedObject(
                    DirectivesClosedObjectTest.BASE,
                    "",
                    "",
                    Collections.emptyList()
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[@base='base' and not(@as) and not(@name)]")
        );
    }
}
