/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        final int actual = new XmlValue(
            new XmlNode("<o><o>00 00 00 00 00 00 04 21</o></o>")
        ).integer();
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
                new XmlNode(new Xembler(new DirectivesValue(origin)).xmlQuietly())
            ).object(),
            Matchers.equalTo(origin)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Φ", "Ψ", "Ω", "Ϊ", "Ϋ", "ά", "έ", "ή", "ί", "ΰ", "α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ",
    })
    void decodesUnicodeCharacters(final String unicode) throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't decode unicode characters",
            new XmlValue(
                new XmlNode(
                    new Xembler(
                        new DirectivesValue(unicode)
                    ).xml()
                )
            ).string(),
            Matchers.equalTo(unicode)
        );
    }

    /**
     * Arguments for {@link XmlValue#decodesEncodesCorrectly(Object, String)}.
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
            Arguments.of(new AllLabels().label("some"))
        );
    }
}
