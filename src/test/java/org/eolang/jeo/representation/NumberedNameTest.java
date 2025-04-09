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
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test case for {@link NumberedName}.
 * @since 0.9
 */
final class NumberedNameTest {

    @ParameterizedTest
    @CsvSource({
        "foo, 1, foo",
        "foo, 2, foo-2",
        "foo, 3, foo-3"
    })
    void formatsName(final String name, final int number, final String expected) {
        MatcherAssert.assertThat(
            String.format(
                "Incorrectly formatted name, expected %s, got %s",
                expected,
                new NumberedName(number, name)
            ),
            new NumberedName(number, name).toString(),
            Matchers.equalTo(expected)
        );
    }

    @Test
    void throwsExceptionWhenNumberIsLessThanOne() {
        MatcherAssert.assertThat(
            "We expect that exception message will be human-readable",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new NumberedName(0, "foo").toString(),
                "Exception was not thrown"
            ).getMessage(),
            Matchers.equalTo("Number must be greater than 0, but was: 0")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "foo, foo",
        "foo-2, foo",
        "bar-3, bar",
        "foobar-4, foobar"
    })
    void decodesName(final String encoded, final String expected) {
        MatcherAssert.assertThat(
            "Incorrectly decoded name",
            new NumberedName(encoded).plain(),
            Matchers.equalTo(expected)
        );
    }
}
