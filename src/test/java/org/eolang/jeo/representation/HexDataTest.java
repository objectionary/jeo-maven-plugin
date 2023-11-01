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
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link org.eolang.jeo.representation.HexData}.
 * @since 0.1.
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
            Arguments.of(0.1d, "float")
        );
    }

}