/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.PrefixedName;
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
                "/o[contains(@name,'unknown')]",
                "/o/o[@name='access']",
                "/o/o[@name='descriptor']",
                "/o/o[@name='signature']",
                "/o/o[contains(@base,'number') and @name='value']"
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
                "/o[contains(@name,'serialVersionUID')]",
                "/o/o[@name='access']",
                "/o/o[@name='descriptor']",
                "/o/o[@name='signature']",
                "/o/o[@name='value']"
            )
        );
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "Φ", "Ψ", "Ω", "Δ", "Σ", "Θ", "Λ", "Ξ", "Π", "Υ"
        }
    )
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
                ).directives(new Format())
            ).xml(),
            XhtmlMatchers.hasXPaths(
                new JeoBaseXpath("/o", "field").toXpath(),
                String.format("/o[@name='%s']", new PrefixedName(original).encode())
            )
        );
    }

    @Test
    void omitsFieldModifiersWhenDisabled() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Field should omit modifiers object when Format.MODIFIERS is false",
            new Xembler(
                new DirectivesField(
                    new Format(Format.MODIFIERS, false),
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                    "flag",
                    "I",
                    "",
                    1,
                    new DirectivesAnnotations("annotations-flag")
                )
            ).xml(),
            XhtmlMatchers.hasXPath("/o[not(o[@name='modifiers'])]")
        );
    }

    @Test
    void emitsFieldModifiersWhenEnabled() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Field should include modifiers object with field flags when Format.MODIFIERS is true",
            new Xembler(
                new DirectivesField(
                    new Format(Format.MODIFIERS, true),
                    Opcodes.ACC_PUBLIC
                        | Opcodes.ACC_STATIC
                        | Opcodes.ACC_TRANSIENT
                        | Opcodes.ACC_VOLATILE,
                    "flag",
                    "I",
                    "",
                    1,
                    new DirectivesAnnotations("annotations-flag")
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "/o/o[@name='modifiers']/o[@name='public' and contains(@base,'true')]",
                "/o/o[@name='modifiers']/o[@name='static' and contains(@base,'true')]",
                "/o/o[@name='modifiers']/o[@name='transient' and contains(@base,'true')]",
                "/o/o[@name='modifiers']/o[@name='volatile' and contains(@base,'true')]",
                "/o/o[@name='modifiers']/o[@name='private' and contains(@base,'false')]"
            )
        );
    }
}
