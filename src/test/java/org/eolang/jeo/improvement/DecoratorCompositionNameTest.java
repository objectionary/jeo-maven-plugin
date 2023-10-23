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
package org.eolang.jeo.improvement;

import org.eolang.jeo.Representation;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test case for {@link DecoratorCompositionName}.
 * @since 0.1
 */
class DecoratorCompositionNameTest {

    @ParameterizedTest
    @CsvSource({
        "A, B, AB",
        "Foo, Bar, FooBar",
        "org.eolang.A, org.eolang.B, org/eolang/AB",
        "a.Foo, b.Bar, b/FooBar"
    })
    void retrivesCombinedName(
        final String decorated,
        final String decorator,
        final String expected
    ) {
        final String actual = new DecoratorCompositionName(
            new Representation.Named(decorated),
            new Representation.Named(decorator)
        ).value();
        MatcherAssert.assertThat(
            String.format(
                "Generated name '%s' is not as expected '%s' for decorated class '%s' and its decorator '%s'",
                actual,
                expected,
                decorated,
                decorator
            ),
            actual,
            Matchers.equalTo(expected)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "A, B, 41 42",
        "Foo, Bar, 46 6F 6F 42 61 72",
        "org.eolang.A, org.eolang.B, 6F 72 67 2F 65 6F 6C 61 6E 67 2F 41 42",
        "a.Foo, b.Bar, 62 2F 46 6F 6F 42 61 72"
    })
    void retrievesHexDecimalRepresentation(
        final String decorated,
        final String decorator,
        final String expected
    ) {
        final String actual = new DecoratorCompositionName(
            new Representation.Named(decorated),
            new Representation.Named(decorator)
        ).hex();
        MatcherAssert.assertThat(
            String.format(
                "Generated name '%s' in hex form is not as expected '%s' for decorated class '%s' and its decorator '%s'",
                actual,
                expected,
                decorated,
                decorator
            ),
            actual,
            Matchers.equalTo(expected)
        );
    }
}