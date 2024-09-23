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

import org.eolang.jeo.representation.bytecode.InnerClass;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XmlAttributes}.
 * @since 0.6
 */
final class XmlAttributesTest {

    /**
     * Example XMIR of attributes.
     */
    private static final String XMIR = String.join(
        "\n",
        "<o base='seq1' name='attributes'>",
        "   <o base='org.eolang.jeo.inner-class'>",
        "      <o base='org.eolang.jeo.string'><o base='bytes' data='bytes'>6E 61 6D 65</o></o>",
        "      <o base='org.eolang.jeo.string'><o base='bytes' data='bytes'>6F 75 74 65 72</o></o>",
        "      <o base='org.eolang.jeo.string'><o base='bytes' data='bytes'>69 6E 6E 65 72</o></o>",
        "      <o base='org.eolang.jeo.int'><o base='bytes' data='bytes'>00 00 00 00 00 00 00 00</o></o>",
        "   </o>",
        "</o>"
    );

    @Test
    void convertsToBytecode() {
        MatcherAssert.assertThat(
            "We expect the attributes to be converted to a correct bytecode domain class",
            new XmlAttributes(new XmlNode(XmlAttributesTest.XMIR)).attributes(),
            Matchers.contains(
                new InnerClass("name", "outer", "inner", 0)
            )
        );
    }
}
