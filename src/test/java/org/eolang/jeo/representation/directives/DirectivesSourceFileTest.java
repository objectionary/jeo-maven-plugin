/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesSourceFile}.
 * @since 0.15.0
 */
final class DirectivesSourceFileTest {

    @Test
    void convertsSourceFileToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesSourceFile(new Format(), "Foo.java", "debug")
        ).xml();
        MatcherAssert.assertThat(
            "We expect the source file attribute to keep the source name and the debug string",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "source-file").toXpath(),
                "/o[contains(@name,'source-file')]",
                "/o/o[@name='source']",
                "/o/o[@name='debug']"
            )
        );
    }
}
