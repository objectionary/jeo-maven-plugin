/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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
package org.eolang.jeo.representation;

import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test cases for {@link org.eolang.jeo.representation.HexData}.
 * @since 0.1
 */
class HexDataTest {

    @MethodSource("types")
    @ParameterizedTest
    void determinesTypeCorrectly(final Object data, final String type) {
        MatcherAssert.assertThat(
            String.format(
                "Expected and actual types differ, the type for '%s' should be '%s'",
                data,
                type
            ),
            new HexData(data).type(),
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
            new HexData(data).value(),
            Matchers.equalTo(hex)
        );
    }

    /**
     * Arguments for {@link HexDataTest#determinesTypeCorrectly(Object, String)} test.
     * @return Stream of arguments.
     */
    static Stream<Arguments> types() {
        return Stream.of(
            Arguments.of(1, "int"),
            Arguments.of("Hello!", "string"),
            Arguments.of(new byte[]{1, 2, 3}, "bytes"),
            Arguments.of(true, "bool"),
            Arguments.of(0.1d, "float"),
            Arguments.of(HexDataTest.class, "reference")
        );
    }

    /**
     * Arguments for {@link HexDataTest#convertsRawDataIntoHexString(Object, String)}.
     * Example for reference - {@link HexDataTest} is "org.eolang.jeo.representation.HexDataTest"
     * @return Stream of arguments.
     */
    static Stream<Arguments> values() {
        return Stream.of(
            Arguments.of(10, "00 00 00 00 00 00 00 0A"),
            Arguments.of("Hello!", "48 65 6C 6C 6F 21"),
            Arguments.of(new byte[]{1, 2, 3}, "01 02 03"),
            Arguments.of(true, "01"),
            Arguments.of(false, "00"),
            Arguments.of(0.1d, "3F B9 99 99 99 99 99 9A"),
            Arguments.of(
                HexDataTest.class,
                "6F 72 67 2E 65 6F 6C 61 6E 67 2E 6A 65 6F 2E 72 65 70 72 65 73 65 6E 74 61 74 69 6F 6E 2E 48 65 78 44 61 74 61 54 65 73 74"
            )
        );
    }
}
