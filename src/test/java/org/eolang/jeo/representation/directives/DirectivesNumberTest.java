/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
import org.junit.jupiter.params.provider.Arguments;

/**
 * Test case for {@link DirectivesNumber}.
 * @since 0.8
 */
final class DirectivesNumberTest {

//    @ParameterizedTest
//    @MethodSource("numbers")
//    void convertsDifferentNumbersToBytes(
//        final Number value, final String bytes
//    ) throws ImpossibleModificationException {
//        final String xml = new Xembler(new DirectivesNumberBytes(value)).xml();
//        MatcherAssert.assertThat(
//            String.format(
//                "We expect that number will be converted to the correct XMIR, but got: %n%s%n",
//                xml
//            ),
//            xml,
//            XhtmlMatchers.hasXPaths(
//                "/o[@base='org.eolang.number']",
//                String.format(
//                    "/o[@base='org.eolang.number']/o[@base='org.eolang.bytes' and text()='%s']",
//                    bytes
//                )
//            )
//        );
//    }

    /**
     * Provide numbers.
     * @return Numbers.
     */
    static Stream<Arguments> numbers() {
        final String same = "3F-F0-00-00-00-00-00-00";
        return Stream.of(
            Arguments.of(1, same),
            Arguments.of(1L, same),
            Arguments.of(1.0, same),
            Arguments.of(1.0f, same),
            Arguments.of(1.0d, same),
            Arguments.of((byte) 1, same),
            Arguments.of((short) 1, same)
        );
    }


}