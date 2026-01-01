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
 * Tests for {@link Modifiers}.
 * @since 0.15.0
 */
final class DirectivesClassModifiersTest {

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        final DirectivesClassModifiers dirs = new DirectivesClassModifiers(
            new Format(), Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_ENUM
        );
        final String xml = new Xembler(dirs).xml();
        MatcherAssert.assertThat(
            "We expect class access modifiers in the directives with correct values",
            xml,
            XhtmlMatchers.hasXPaths(
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'public') and contains(@base, 'true')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'private') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'protected') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'final') and contains(@base, 'true')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'super') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'interface') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'abstract') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'synthetic') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'annotation') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'enum') and contains(@base, 'true')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'mandated') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'module') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'record') and contains(@base, 'false')]",
                "/o[contains(@name, 'modifiers')]/o[contains(@name, 'deprecated') and contains(@base, 'false')]"
            )
        );
    }
}
