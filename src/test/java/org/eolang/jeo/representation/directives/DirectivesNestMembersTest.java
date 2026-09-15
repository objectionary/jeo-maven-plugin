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
 * Test cases for {@link DirectivesNestMembers}.
 * @since 0.15.0
 */
final class DirectivesNestMembersTest {

    @Test
    void convertsNestMembersToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesNestMembers(new Format(), Arrays.asList("a", "b"))
        ).xml();
        MatcherAssert.assertThat(
            "We expect the nest members attribute to contain the member list",
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "nest-members").toXpath(),
                "/o[contains(@name,'nest-members')]",
                "/o/o[@name='members']"
            )
        );
    }
}
