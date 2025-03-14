/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeField;
import org.eolang.jeo.representation.directives.DirectivesAnnotation;
import org.eolang.jeo.representation.directives.DirectivesField;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlField}.
 *
 * @since 0.3
 */
final class XmlFieldTest {

    @Test
    void parsesXmirFieldSuccessfully() throws ImpossibleModificationException {
        final int access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
        final String name = "serialVersionUID";
        final String descriptor = "J";
        final long value = 7_099_057_708_183_571_937L;
        MatcherAssert.assertThat(
            "Failed to parse XMIR field",
            new XmlField(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesField(
                            access,
                            name,
                            descriptor,
                            "",
                            value
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeField(
                    name,
                    descriptor,
                    null,
                    value,
                    access,
                    new BytecodeAnnotations()
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("values")
    void parsesFieldWithValues(final Object value) throws ImpossibleModificationException {
        final int access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
        final String name = "serialVersionUID";
        final String descriptor = "Ljava/lang/String;";
        MatcherAssert.assertThat(
            "We expect the value to be the same",
            new XmlField(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesField(access, name, descriptor, null, value)
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeField(
                    name, descriptor, null, value, access
                )
            )
        );
    }

    @Test
    void retrievesFieldAnnotations() throws ImpossibleModificationException {
        final String override = "java/lang/Override";
        final String safe = "java/lang/SafeVarargs";
        MatcherAssert.assertThat(
            "Annotations are not found",
            new XmlField(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesField(
                            new DirectivesAnnotation(override, true),
                            new DirectivesAnnotation(safe, true)
                        )
                    ).xml()
                )
            ).bytecode(),
            Matchers.equalTo(
                new BytecodeField(
                    "unknown",
                    "I",
                    null,
                    0,
                    1,
                    new BytecodeAnnotations(
                        new BytecodeAnnotation(override, true),
                        new BytecodeAnnotation(safe, true)
                    )
                )
            )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Φ", "Ψ", "Ω", "Δ", "Σ", "Θ", "Λ", "Ξ", "Π", "Υ", "\u03A3", "\u03A6", "\u03A8", "\u03A9"
    })
    void parsesXmirFieldWithUnicodeCharacterInTheName(final String original)
        throws ImpossibleModificationException {
        final BytecodeField expected = new BytecodeField(
            original,
            "I",
            null,
            0,
            Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL
        );
        MatcherAssert.assertThat(
            "We expect the field name with unicode characters to be successfully parsed from directives",
            new XmlField(new NativeXmlNode(new Xembler(expected.directives()).xml())).bytecode(),
            Matchers.equalTo(expected)
        );
    }

    /**
     * Provide values.
     * Test cases for {@link #parsesFieldWithValues(Object)}.
     * @return Values.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Object> values() {
        return Stream.of("7099057708183571937", "", null);
    }
}
