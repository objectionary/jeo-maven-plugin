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
package org.eolang.jeo.representation.directives;

import java.util.stream.Stream;
import org.eolang.jeo.matchers.SameXml;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Type;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesValue}.
 * @since 0.3
 */
@SuppressWarnings("PMD.TooManyMethods")
final class DirectivesValueTest {

    @Test
    void convertsInteger() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that integer value is converted to the correct XMIR",
            new Xembler(new DirectivesValue("access", 42)).xml(),
            new SameXml(
                String.join(
                    "\n",
                    "<o base='jeo.int' name='access'>",
                    "  <o base='org.eolang.bytes' data='bytes'>00 00 00 00 00 00 00 2A</o>",
                    "</o>"
                )
            )
        );
    }

    @Test
    void convertsLabel() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Converts label to XML",
            new Xembler(
                new DirectivesValue(
                    new AllLabels().label("some-random")
                ),
                new Transformers.Node()
            ).xml(),
            new SameXml(
                "<o base='jeo.label'><o base='org.eolang.bytes' data='bytes'>73 6F 6D 65 2D 72 61 6E 64 6F 6D</o></o>"
            )
        );
    }

    @MethodSource("types")
    @ParameterizedTest
    void determinesTypeCorrectly(final Object data, final String type) {
        MatcherAssert.assertThat(
            String.format(
                "Expected and actual types differ, the type for '%s' should be '%s'",
                data,
                type
            ),
            new DirectivesValue(data).type(),
            Matchers.equalTo(type)
        );
    }

    @MethodSource("values")
    @ParameterizedTest
    void convertsRawDataIntoHexString(final Object data, final String hex) {
        MatcherAssert.assertThat(
            String.format(
                String.format(
                    "Expected and actual hex values differ, the value for '%s' should be '%s'",
                    data,
                    hex
                )
            ),
            new DirectivesValue(data).hex(),
            Matchers.equalTo(hex)
        );
    }

    @Test
    void convertsRawPrimitiveDataToHexString() {
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for '10' should be '00 00 00 00 00 00 00 0A'",
            new DirectivesValue(10).hex(),
            Matchers.equalTo("00 00 00 00 00 00 00 0A")
        );
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for '0.1d' should be '3F B9 99 99 99 99 99 9A'",
            new DirectivesValue(0.1d).hex(),
            Matchers.equalTo("3F B9 99 99 99 99 99 9A")
        );
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for '0.1f' should be '3D CC CC CD'",
            new DirectivesValue(0.1f).hex(),
            Matchers.equalTo("3D CC CC CD")
        );
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for 'true' should be '01'",
            new DirectivesValue(true).hex(),
            Matchers.equalTo("01")
        );
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for 'false' should be '00'",
            new DirectivesValue(false).hex(),
            Matchers.equalTo("00")
        );
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for 'Hello!' should be '48 65 6C 6C 6F 21'",
            new DirectivesValue(11L).type(),
            Matchers.equalTo("long")
        );
    }

    @Test
    void encodesType() {
        final String value = new DirectivesValue(Type.INT_TYPE).hex();
        MatcherAssert.assertThat(
            "Expected and actual hex values differ, the value for 'Type.INT_TYPE' should be '69 6E 74'",
            value,
            Matchers.equalTo("49")
        );
    }

    /**
     * Arguments for {@link DirectivesValueTest#determinesTypeCorrectly(Object, String)} test.
     * @return Stream of arguments.
     */
    static Stream<Arguments> types() {
        return Stream.of(
            Arguments.of(1, "int"),
            Arguments.of("Hello!", "string"),
            Arguments.of(new byte[]{1, 2, 3}, "bytes"),
            Arguments.of(true, "bool"),
            Arguments.of(0.1f, "float"),
            Arguments.of(0.1d, "double"),
            Arguments.of(DirectivesValue.class, "class"),
            Arguments.of(' ', "char")
        );
    }

    /**
     * Arguments for {@link DirectivesValueTest#convertsRawDataIntoHexString(Object, String)}.
     * @return Stream of arguments.
     */
    static Stream<Arguments> values() {
        return Stream.of(
            Arguments.of(10, "00 00 00 00 00 00 00 0A"),
            Arguments.of("Hello!", "48 65 6C 6C 6F 21"),
            Arguments.of(new byte[]{1, 2, 3}, "01 02 03"),
            Arguments.of(true, "01"),
            Arguments.of(false, "00"),
            Arguments.of('a', "00 61"),
            Arguments.of(0.1d, "3F B9 99 99 99 99 99 9A"),
            Arguments.of(
                DirectivesValueTest.class,
                "6F 72 67 2F 65 6F 6C 61 6E 67 2F 6A 65 6F 2F 72 65 70 72 65 73 65 6E 74 61 74 69 6F 6E 2F 64 69 72 65 63 74 69 76 65 73 2F 44 69 72 65 63 74 69 76 65 73 56 61 6C 75 65 54 65 73 74"
            )
        );
    }
}
