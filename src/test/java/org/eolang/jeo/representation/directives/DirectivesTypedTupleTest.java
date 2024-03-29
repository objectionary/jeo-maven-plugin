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
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesTypedTuple}.
 *
 * @since 0.3
 */
final class DirectivesTypedTupleTest {


    @Test
    void convertsInts() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert ints to XML directives correctly",
            new Xembler(new DirectivesTypedTuple("name", new int[]{1, 2, 3})).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[count(o)=4]",
                "./o/o[1][text()='5B 49']"
            )
        );
    }

    @Test
    void convertsLongs() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert longs to XML directives correctly",
            new Xembler(
                new DirectivesTypedTuple("longs", new long[]{1L, 2L, 3L})
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[count(o)=4]",
                "./o/o[1][text()='5B 4A']"
            )
        );
    }

    @Test
    void convertsStrings() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert strings to XML directives correctly",
            new Xembler(new DirectivesTypedTuple("strings", new String[]{"a", "b", "c"})).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[count(o)=4]",
                "./o/o[1][text()='5B 4C 6A 61 76 61 2E 6C 61 6E 67 2E 53 74 72 69 6E 67 3B']"
            )
        );
    }
}