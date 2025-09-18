/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesMethodModifiers}.
 * @since 0.14.0
 */
final class DirectivesMethodModifiersTest {

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect access modifiers in the directives with correct values",
            new Xembler(
                new DirectivesMethodModifiers(new Format(), Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'public') and contains(@base, 'true')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'private') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'protected') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'static') and contains(@base, 'true')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'final') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'synchronized') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'bridge') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'varargs') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'native') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'abstract') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'strict') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'synthetic') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'mandated') and contains(@base, 'false')]"
            )
        );
    }
}
