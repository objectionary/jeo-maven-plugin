/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesAbsractObject}.
 * @since 0.11.0
 */
final class DirectivesAbsractObjectTest {

    /**
     * Base name for the abstract object.
     */
    private static final String BASE = "base";

    @Test
    void createsAbstractObjectWithBaseOnly() {
        MatcherAssert.assertThat(
            "We expect the abstract object to have only the base EO attribute",
            new Xembler(
                new DirectivesAbsractObject(
                    DirectivesAbsractObjectTest.BASE, Collections.emptyList()
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[not(@base)]/o[@as='base']")
        );
    }

    @Test
    void createsAbstractObjectWithBaseAndAs() {
        MatcherAssert.assertThat(
            "We expect the abstract object to have base EO attribute and as XML attribute",
            new Xembler(
                new DirectivesAbsractObject(
                    DirectivesAbsractObjectTest.BASE, "asValue", Collections.emptyList()
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[@as='asValue' and not(@base)]/o[@as='base']")
        );
    }

    @Test
    void createsAbstractObjectWithBaseAsAndName() {
        MatcherAssert.assertThat(
            "We expect the abstract object to have base EO attribute, as and name XML attributes",
            new Xembler(
                new DirectivesAbsractObject(
                    DirectivesAbsractObjectTest.BASE,
                    "asValue",
                    "nameValue",
                    new Directives().add("inner").up()
                )
            ).xmlQuietly(),
            Matchers.allOf(
                XhtmlMatchers.hasXPath("/o[@as='asValue' and @name='nameValue']/o[@as='base']"),
                XhtmlMatchers.hasXPath("/o[@as='asValue' and @name='nameValue']/inner")
            )
        );
    }

    @Test
    void createsAbstractObjectWithInternalDirectives() {
        MatcherAssert.assertThat(
            "We expect the abstract object to contain internal directives",
            new Xembler(
                new DirectivesAbsractObject(
                    DirectivesAbsractObjectTest.BASE, new Directives().add("inner")
                )
            ).xmlQuietly(),
            Matchers.allOf(
                XhtmlMatchers.hasXPath("/o/o[@as='base']"),
                XhtmlMatchers.hasXPath("/o/inner")
            )
        );
    }

    @Test
    void doesNotAddEmptyAsOrNameAttributes() {
        MatcherAssert.assertThat(
            "We expect the abstract object to not have as or name XML attributes when they are empty",
            new Xembler(
                new DirectivesAbsractObject(
                    DirectivesAbsractObjectTest.BASE, "", "", Collections.emptyList()
                )
            ).xmlQuietly(),
            XhtmlMatchers.hasXPath("/o[not(@as) and not(@name)]/o[@as='base']")
        );
    }
}
