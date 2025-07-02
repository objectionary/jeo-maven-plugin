/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.bytecode.BytecodeField;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test cases for {@link DirectivesField}.
 * This class verifies the generation of field directives for Java class fields,
 * including various field properties and access modifiers.
 *
 * @since 0.3.0
 */
final class DirectivesFieldTest {

    @Test
    void convertsDefaultFieldToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(new DirectivesField()).xml();
        MatcherAssert.assertThat(
            String.format(
                "Incorrect transformation of default field to directives, received invalid XMIR: %n%s",
                xml
            ),
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "field").toXpath(),
                "/o[contains(@as,'unknown')]",
                "/o/o[@as='access-unknown']",
                "/o/o[@as='descriptor-unknown']",
                "/o/o[@as='signature-unknown']",
                "/o/o[contains(@base,'number') and @as='value-unknown']"
            )
        );
    }

    @Test
    void convertsLongFieldToDirectives() throws ImpossibleModificationException {
        final String xml = new Xembler(
            new DirectivesField(
                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                "serialVersionUID",
                "J",
                "",
                7_099_057_708_183_571_937L
            )
        ).xml();
        MatcherAssert.assertThat(
            String.format(
                "Incorrect transformation of long field to directives, received invalid XMIR: %n%s",
                xml
            ),
            xml,
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "field").toXpath(),
                "/o[contains(@as,'serialVersionUID')]",
                "/o/o[@as='access-serialVersionUID']",
                "/o/o[@as='descriptor-serialVersionUID']",
                "/o/o[@as='signature-serialVersionUID']",
                "/o/o[@as='value-serialVersionUID']"
            )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Φ", "Ψ", "Ω", "Δ", "Σ", "Θ", "Λ", "Ξ", "Π", "Υ", "\u03A3", "\u03A6", "\u03A8", "\u03A9"
    })
    void convertsDirectivesFieldWithUnicodeName(
        final String original
    ) throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect the field name with unicode characters to be successfully converted to directives",
            new Xembler(
                new BytecodeField(
                    original,
                    "I",
                    "",
                    0,
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL
                ).directives()
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "field").toXpath(),
                String.format("/o[contains(@as,'%s')]", original)
            )
        );
    }
}
