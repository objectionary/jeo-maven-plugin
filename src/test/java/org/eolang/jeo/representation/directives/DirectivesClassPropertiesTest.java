/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link org.eolang.jeo.representation.directives.DirectivesClassProperties}.
 * @since 0.1.0
 */
final class DirectivesClassPropertiesTest {

    @Test
    void createsDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create proper xml",
            new Xembler(
                new Directives()
                    .add("o")
                    .append(
                        new DirectivesClassProperties(
                            1,
                            "org/eolang/SomeClass",
                            "org/eolang/SomeInterface"
                        )
                    ).up()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o/o[contains(@base,'number') and contains(@name,'version')]",
                "/o/o[contains(@base,'number') and contains(@name,'access')]",
                "/o/o[contains(@base,'string') and contains(@name,'supername')]",
                new JeoBaseXpath("./o/o", "seq.of1").toXpath(),
                "/o/o[contains(@name,'interfaces')]"
            )
        );
    }

    @Test
    void addsClassModifiers() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect class access modifiers to be added to the directives",
            new Xembler(
                new Directives()
                    .add("o")
                    .append(
                        new DirectivesClassProperties(
                            new Format(Format.MODIFIERS, true),
                            Opcodes.ACC_INTERFACE
                        )
                    ).up()
            ).xml(),
            XhtmlMatchers.hasXPath(
                "//o[contains(@name, 'modifiers')]/o[contains(@name, 'interface') and contains(@base, 'true')]"
            )
        );
    }

    @Test
    void ignoresClassModifiers() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect class access modifiers to be avoided by default",
            new Xembler(
                new Directives()
                    .add("o")
                    .append(new DirectivesClassProperties(Opcodes.ACC_INTERFACE))
                    .up()
            ).xml(),
            Matchers.not(
                XhtmlMatchers.hasXPath(
                    "//o[contains(@name, 'modifiers')]/o[contains(@name, 'interface') and contains(@base, 'true')]"
                )
            )
        );
    }
}
