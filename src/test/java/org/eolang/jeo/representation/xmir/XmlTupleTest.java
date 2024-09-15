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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XmlTuple}.
 *
 * @since 0.3
 */
final class XmlTupleTest {

    @Test
    void parsesIntArray() {
        MatcherAssert.assertThat(
            "Can't parse int array",
            new XmlTuple(
                "<o base='tuple' name='name' star=''>",
                "   <o base='string' data='bytes' name='type'>5B 49</o>",
                "   <o base='int' data='bytes'>00 00 00 00 00 00 00 01</o>",
                "   <o base='int' data='bytes'>00 00 00 00 00 00 00 02</o>",
                "   <o base='int' data='bytes'>00 00 00 00 00 00 00 03</o>",
                "</o>"
            ).asObject(),
            Matchers.equalTo(
                new int[]{1, 2, 3}
            )
        );
    }

    @Test
    void parsesLongArray() {
        MatcherAssert.assertThat(
            "Can't parse long array",
            new XmlTuple(
                "<o base='tuple' name='longs' star=''>",
                "   <o base='string' data='bytes' name='type'>5B 4A</o>",
                "   <o base='long' data='bytes'>00 00 00 00 00 00 00 01</o>",
                "   <o base='long' data='bytes'>00 00 00 00 00 00 00 02</o>",
                "   <o base='long' data='bytes'>00 00 00 00 00 00 00 03</o>",
                "</o>"
            ).asObject(),
            Matchers.equalTo(
                new long[]{1L, 2L, 3L}
            )
        );
    }

    @Test
    void parsesStringArray() {
        MatcherAssert.assertThat(
            "Can't parse string array",
            new XmlTuple(
                "<o base='tuple' name='strings' star=''>",
                "   <o base='string' data='bytes' name='type'>5B 4C 6A 61 76 61 2E 6C 61 6E 67 2E 53 74 72 69 6E 67 3B</o>",
                "   <o base='string' data='bytes'>61</o>",
                "   <o base='string' data='bytes'>62</o>",
                "   <o base='string' data='bytes'>63</o>",
                "</o>"
            ).asObject(),
            Matchers.equalTo(
                new String[]{"a", "b", "c"}
            )
        );
    }

}
