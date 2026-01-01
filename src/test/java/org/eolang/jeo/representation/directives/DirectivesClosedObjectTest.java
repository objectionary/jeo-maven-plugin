/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
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

    @Test
    void createsClosedObjectWithBaseOnly() {
        MatcherAssert.assertThat(
            "We expect the closed object to have only the base attribute",
            new Xembler(
                new DirectivesClosedObject("closed", Collections.emptyList())
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[@base='closed' and not(@as) and not(@name)]")
        );
    }

    @Test
    void createsClosedObjectWithBaseAndAs() {
        MatcherAssert.assertThat(
            "We expect the closed object to have base and as attributes",
            new Xembler(
                new DirectivesClosedObject(
                    "with-base-and-as", "asValue", Collections.emptyList()
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[@base='with-base-and-as' and @as='asValue' and not(@name)]")
        );
    }

    @Test
    void createsClosedObjectWithBaseAsAndName() {
        MatcherAssert.assertThat(
            "We expect the closed object to have base, as, and name attributes",
            new Xembler(
                new DirectivesClosedObject(
                    "with-base-as-and-name",
                    "asValue",
                    "nameValue",
                    Collections.emptyList()
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath(
                "/o[@base='with-base-as-and-name' and @as='asValue' and @name='nameValue']"
            )
        );
    }

    @Test
    void createsClosedObjectWithInternalDirectives() {
        MatcherAssert.assertThat(
            "We expect the closed object to contain internal directives",
            new Xembler(
                new DirectivesClosedObject(
                    "with-internal-directives",
                    new Directives().add("inner")
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[@base='with-internal-directives']/inner")
        );
    }

    @Test
    void doesNotAddEmptyAsOrNameAttributes() {
        MatcherAssert.assertThat(
            "We expect the closed object to not have as or name attributes when they are empty",
            new Xembler(
                new DirectivesClosedObject(
                    "with-empty-as-and-name",
                    "",
                    "",
                    Collections.emptyList()
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[@base='with-empty-as-and-name' and not(@as) and not(@name)]")
        );
    }
}
