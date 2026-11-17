/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
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
 * Tests for {@link DirectivesFieldModifiers}.
 *
 * @since 0.15.0
 */
final class DirectivesFieldModifiersTest {

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect field access modifiers in the directives with correct values",
            new Xembler(
                new DirectivesFieldModifiers(
                    new Format(),
                    Opcodes.ACC_PUBLIC
                        | Opcodes.ACC_STATIC
                        | Opcodes.ACC_FINAL
                        | Opcodes.ACC_TRANSIENT
                        | Opcodes.ACC_VOLATILE
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'public') and contains(@base, 'true')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'private') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'protected') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'static') and contains(@base, 'true')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'final') and contains(@base, 'true')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'volatile') and contains(@base, 'true')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'transient') and contains(@base, 'true')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'synthetic') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'enum') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'deprecated') and contains(@base, 'false')]"
            )
        );
    }
}
