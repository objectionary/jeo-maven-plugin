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
package org.eolang.jeo.representation.xmir;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XmlTryCatchEntry}.
 * @since 0.1
 */
final class XmlTryCatchEntryTest {

    @Test
    void findsType() {
        MatcherAssert.assertThat(
            "Can't find type",
            new XmlTryCatchEntry(
                new XmlNode(
                    String.join(
                        "\n",
                        "<o base=\"trycatch\">\n",
                        "  <o base=\"label\" name=\"start\" data=\"bytes\">30 65 65 66 66 62 37 37 2D 34 64 32 62 2D 34 63 31 38 2D 39 32 32 39 2D 36 32 65 39 66 61 66 39 34 61 34 34</o>\n",
                        "  <o base=\"label\" name=\"end\" data=\"bytes\">62 31 65 65 38 61 34 32 2D 37 63 39 63 2D 34 63 66 39 2D 61 63 63 65 2D 39 35 62 39 38 36 38 34 34 65 36 35</o>\n",
                        "  <o base=\"label\" name=\"handler\" data=\"bytes\">62 31 65 65 38 61 34 32 2D 37 63 39 63 2D 34 63 66 39 2D 61 63 63 65 2D 39 35 62 39 38 36 38 34 34 65 36 35</o>\n",
                        "  <o base=\"string\" data=\"bytes\" name=\"type\">6A 61 76 61 2F 69 6F 2F 49 4F 45 78 63 65 70 74 69 6F 6E</o>\n",
                        "</o>"
                    )
                )
            ).type().isPresent(),
            Matchers.is(true)
        );
    }

//    @Test
//    void findsStartLabel() {
//        MatcherAssert.assertThat(
//            "Can't find 'start' label",
//            new XmlTryCatchEntry(
//                new XmlNode(
//                    String.join(
//                        "\n",
//                        "<o base=\"trycatch\">\n",
//                        "  <o base=\"label\" name=\"start\" data=\"bytes\">30 65 65 66 66 62 37 37 2D 34 64 32 62 2D 34 63 31 38 2D 39 32 32 39 2D 36 32 65 39 66 61 66 39 34 61 34 34</o>\n",
//                        "  <o base=\"label\" name=\"end\" data=\"bytes\">62 31 65 65 38 61 34 32 2D 37 63 39 63 2D 34 63 66 39 2D 61 63 63 65 2D 39 35 62 39 38 36 38 34 34 65 36 35</o>\n",
//                        "  <o base=\"label\" name=\"handler\" data=\"bytes\">62 31 65 65 38 61 34 32 2D 37 63 39 63 2D 34 63 66 39 2D 61 63 63 65 2D 39 35 62 39 38 36 38 34 34 65 36 35</o>\n",
//                        "  <o base=\"string\" data=\"bytes\" name=\"type\">6A 61 76 61 2F 69 6F 2F 49 4F 45 78 63 65 70 74 69 6F 6E</o>\n",
//                        "</o>"
//                    )
//                )
//            ).label("start").isPresent(),
//            Matchers.is(true)
//        );
//    }
//
//    @Test
//    void findsEndLabel() {
//        MatcherAssert.assertThat(
//            "Can't find 'end' label",
//            new XmlTryCatchEntry(
//                new XmlNode(
//                    String.join(
//                        "\n",
//                        "<o base=\"trycatch\">\n",
//                        "  <o base=\"label\" name=\"start\" data=\"bytes\">30 65 65 66 66 62 37 37 2D 34 64 32 62 2D 34 63 31 38 2D 39 32 32 39 2D 36 32 65 39 66 61 66 39 34 61 34 34</o>\n",
//                        "  <o base=\"label\" name=\"end\" data=\"bytes\">62 31 65 65 38 61 34 32 2D 37 63 39 63 2D 34 63 66 39 2D 61 63 63 65 2D 39 35 62 39 38 36 38 34 34 65 36 35</o>\n",
//                        "  <o base=\"label\" name=\"handler\" data=\"bytes\">62 31 65 65 38 61 34 32 2D 37 63 39 63 2D 34 63 66 39 2D 61 63 63 65 2D 39 35 62 39 38 36 38 34 34 65 36 35</o>\n",
//                        "  <o base=\"string\" data=\"bytes\" name=\"type\">6A 61 76 61 2F 69 6F 2F 49 4F 45 78 63 65 70 74 69 6F 6E</o>\n",
//                        "</o>"
//                    )
//                )
//            ).label("end").isPresent(),
//            Matchers.is(true)
//        );
//    }
//
//    @Test
//    void findsHandlerLabel() {
//        MatcherAssert.assertThat(
//            "Can't find 'handler' label",
//            new XmlTryCatchEntry(
//                new XmlNode(
//                    String.join(
//                        "\n",
//                        "<o base=\"trycatch\">\n",
//                        "  <o base=\"label\" name=\"start\" data=\"bytes\">30 65 65 66 66 62 37 37 2D 34 64 32 62 2D 34 63 31 38 2D 39 32 32 39 2D 36 32 65 39 66 61 66 39 34 61 34 34</o>\n",
//                        "  <o base=\"label\" name=\"end\" data=\"bytes\">62 31 65 65 38 61 34 32 2D 37 63 39 63 2D 34 63 66 39 2D 61 63 63 65 2D 39 35 62 39 38 36 38 34 34 65 36 35</o>\n",
//                        "  <o base=\"label\" name=\"handler\" data=\"bytes\">62 31 65 65 38 61 34 32 2D 37 63 39 63 2D 34 63 66 39 2D 61 63 63 65 2D 39 35 62 39 38 36 38 34 34 65 36 35</o>\n",
//                        "  <o base=\"string\" data=\"bytes\" name=\"type\">6A 61 76 61 2F 69 6F 2F 49 4F 45 78 63 65 70 74 69 6F 6E</o>\n",
//                        "</o>"
//                    )
//                )
//            ).label("handler").isPresent(),
//            Matchers.is(true)
//        );
//    }
}
