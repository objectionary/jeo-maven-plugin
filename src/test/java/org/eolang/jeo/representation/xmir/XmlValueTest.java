/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.stream.Stream;
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
            new NativeXmlNode(
                "<o base='Q.org.eolang.number'><o base='Q.org.eolang.bytes'><o>40-90-84-00-00-00-00-00</o></o></o>"
            )
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

    @Test
    void initializesFromNamedObject() {
        MatcherAssert.assertThat(
            "XmlValue should be initialized from XmlNamedObject",
            new XmlValue(
                new XmlNamedObject(
                    new JcabiXmlNode(
                        "<o base='Q.org.eolang.string'><o base='Q.org.eolang.bytes'><o>--</o></o></o>"
                    )
                )
            ).string(),
            Matchers.emptyString()
        );
    }

    @Test
    void parsesNumber() {
        MatcherAssert.assertThat(
            "Xml value should parse number from hex string",
            new XmlValue(
                new XmlNamedObject(
                    new JcabiXmlNode(
                        new StringBuilder()
                            .append("<o base=\"Q.org.eolang.number\" name=\"access\">\n")
                            .append("  <o base=\"Q.org.eolang.bytes\">\n")
                            .append("    <o>40-40-00-00-00-00-00-00</o>\n")
                            .append("  </o>\n")
                            .append("</o>").toString()
                    )
                )
            ).object(),
            Matchers.equalTo(0x20)
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
            Arguments.of(Double.MAX_VALUE),
            Arguments.of(0.0d),
            Arguments.of(Double.MIN_VALUE),
            Arguments.of(Float.MAX_VALUE),
            Arguments.of(0.0f),
            Arguments.of(Float.MIN_VALUE),
            Arguments.of(Long.MAX_VALUE),
            Arguments.of(0L),
            Arguments.of(10L),
            Arguments.of(Long.MIN_VALUE),
            Arguments.of(Integer.MAX_VALUE),
            Arguments.of(0),
            Arguments.of(Integer.MIN_VALUE),
            Arguments.of(Short.MAX_VALUE),
            Arguments.of((short) 0),
            Arguments.of(Short.MIN_VALUE),
            Arguments.of(Byte.MAX_VALUE),
            Arguments.of((byte) 0),
            Arguments.of(Byte.MIN_VALUE),
            Arguments.of("org/eolang/jeo/representation/HexDataTest"),
            null
        );
    }
}
