/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.eolang.jeo.representation.directives.DirectivesValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlValue}.
 * @since 0.6
 */
final class XmlValueTest {

    @Test
    void parsesHexStringAsInteger() {
        final int expected = 1057;
        final int actual = (int) new XmlValue(
            new NativeXmlNode("<o base='jeo.int'><o>40-90-84-00-00-00-00-00</o></o>")
        ).object();
        MatcherAssert.assertThat(
            String.format(
                "Can't parse hex string as integer, or the result is wrong; expected %d, got %d",
                expected,
                actual
            ),
            actual,
            Matchers.is(expected)
        );
    }

    @ParameterizedTest
    @MethodSource("values")
    void decodesEncodesCorrectly(final Object origin) {
        MatcherAssert.assertThat(
            "Decoding and encoding are not consistent",
            new XmlValue(
                new NativeXmlNode(new Xembler(new DirectivesValue(origin)).xmlQuietly())
            ).object(),
            Matchers.equalTo(origin)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Φ", "Ψ", "Ω", "Ϊ", "Ϋ", "ά", "έ", "ή", "ί", "ΰ", "α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ"
    })
    void decodesUnicodeCharacters(final String unicode) throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't decode unicode characters",
            new XmlValue(
                new NativeXmlNode(
                    new Xembler(
                        new DirectivesValue(unicode)
                    ).xml()
                )
            ).string(),
            Matchers.equalTo(unicode)
        );
    }

    /**
     * Arguments for {@link XmlValueTest#decodesEncodesCorrectly(Object)}.
     * @return Stream of arguments.
     */
    static Stream<Arguments> values() {
        return Stream.of(
            Arguments.of(10),
            Arguments.of("Hello!"),
            Arguments.of(new byte[]{1, 2, 3}),
            Arguments.of('a'),
            Arguments.of(true),
            Arguments.of(false),
            Arguments.of(0.1d),
            Arguments.of(
                "org/eolang/jeo/representation/HexDataTest"
            ),
            Arguments.of(new BytecodeLabel("some"))
        );
    }
}
