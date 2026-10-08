/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesPermittedSubclasses}.
 * @since 0.15.0
 */
final class DirectivesPermittedSubclassesTest {

    @Test
    void convertsPermittedSubclassesToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesPermittedSubclasses(new Format(), Arrays.asList("a"))
        ).xml();
        MatcherAssert.assertThat(
            "We expect the permitted subclasses attribute to contain the subclass list",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "permitted-subclasses").toXpath(),
                "/o[contains(@name,'permitted-subclasses')]",
                "/o/o[@name='subclasses']"
            )
        );
    }
}
