/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
 * Test case for {@link DirectivesMethodProperties}.
 * @since 0.14.0
 */
final class DirectivesMethodPropertiesTest {

    @Test
    void convertsToXmirWithoutAccessModifiers() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect no access modifiers in the directives",
            new Xembler(
                new Directives().add("o").append(
                    new DirectivesMethodProperties(
                        Opcodes.ACC_PUBLIC,
                        "()V",
                        "",
                        new String[]{},
                        new DirectivesMaxs(1, 1),
                        new DirectivesMethodParams(),
                        new Format(Format.MODIFIERS, false)
                    )
                ).up()
            ).xml(),
            Matchers.not(
                XhtmlMatchers.hasXPath(
                    "/o/o[contains(@name, 'modifiers')]"
                )
            )
        );
    }

    @Test
    void convertsToXmirWithAccessModifiers() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect access modifiers in the directives",
            new Xembler(
                new Directives().add("o").append(
                    new DirectivesMethodProperties(
                        Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                        "()V",
                        "",
                        new String[]{},
                        new DirectivesMaxs(1, 1),
                        new DirectivesMethodParams(),
                        new Format(Format.MODIFIERS, true)
                    )
                ).up()
            ).xml(),
            XhtmlMatchers.hasXPath(
                "/o/o[contains(@name, 'modifiers')]"
            )
        );
    }
}
