/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Collections;
import java.util.stream.Stream;
import org.eolang.jeo.representation.directives.DirectivesDelegateObject;
import org.eolang.jeo.representation.directives.DirectivesValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlDelegateObject}.
 * @since 0.12.0
 */
final class XmlDelegateObjectTest {

    @Test
    void parsesBaseOfDelegateObject() {
        final String base = "object-type-delegate";
        MatcherAssert.assertThat(
            "We expect the delegate object to save base as a first parameter",
            new XmlDelegateObject(
                new JcabiXmlNode(
                    new Xembler(
                        new DirectivesDelegateObject(
                            base,
                            "as-attr",
                            "name-attr",
                            Collections.emptyList()
                        )
                    ).xmlQuietly()
                )
            ).base().orElseThrow(AssertionError::new),
            Matchers.equalTo(base)
        );
    }

    @Test
    void retrievesNameOfDelegateObject() {
        final String name = "delegate-object-name";
        MatcherAssert.assertThat(
            "We expect the delegate object to save name as a second parameter",
            new XmlDelegateObject(
                new JcabiXmlNode(
                    new Xembler(
                        new DirectivesDelegateObject(
                            "base-attr",
                            "as-attr",
                            name,
                            Collections.emptyList()
                        )
                    ).xmlQuietly()
                )
            ).attribute("name").orElseThrow(AssertionError::new),
            Matchers.equalTo(name)
        );
    }

    @Test
    void retrievesFirstChildOfDelegateObject() {
        final String child = "child-object";
        MatcherAssert.assertThat(
            "We expect the delegate object to have a child",
            new XmlValue(
                new XmlDelegateObject(
                    new JcabiXmlNode(
                        new Xembler(
                            new DirectivesDelegateObject(
                                "base-attr",
                                "as-attr",
                                "name-attr",
                                new DirectivesValue(child)
                            )
                        ).xmlQuietly()
                    )
                ).child(0).orElseThrow(AssertionError::new)
            ).string(),
            Matchers.equalTo(child)
        );
    }

    @Test
    void retrievesAllChildrenOfDelegateObject() {
        MatcherAssert.assertThat(
            "We expect the delegate object to have multiple children",
            new XmlDelegateObject(
                new JcabiXmlNode(
                    new Xembler(
                        new DirectivesDelegateObject(
                            "base-attr",
                            "as-attr",
                            "name-attr",
                            Stream.of(
                                new DirectivesValue("first-child"),
                                new DirectivesValue("second-child")
                            ).map(Directives::new).reduce(new Directives(), Directives::append)
                        )
                    ).xmlQuietly()
                )
            ).children().count(),
            Matchers.equalTo(2L)
        );
    }
}
