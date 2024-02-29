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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link org.eolang.jeo.representation.directives.DirectivesClassProperties}.
 *
 * @since 0.1.0
 */
final class DirectivesClassPropertiesTest {

    @Test
    void createsDirectives() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create proper xml",
            new Xembler(
                new Directives()
                    .add("o")
                    .append(
                        new DirectivesClassProperties(
                            1,
                            "org/eolang/SomeClass",
                            "java/lang/Object",
                            "org/eolang/SomeInterface"
                        )
                    ).up()
            ).xml(),
            Matchers.equalTo(
                String.join(
                    "",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n",
                    "<o>\n",
                    "   <o base=\"int\" data=\"bytes\" name=\"access\">00 00 00 01</o>\n",
                    "   <o base=\"string\" data=\"bytes\" name=\"signature\">6F 72 67 2F 65 6F 6C 61 6E 67 2F 53 6F 6D 65 43 6C 61 73 73</o>\n",
                    "   <o base=\"string\" data=\"bytes\" name=\"supername\">6A 61 76 61 2F 6C 61 6E 67 2F 4F 62 6A 65 63 74</o>\n",
                    "   <o base=\"tuple\" name=\"interfaces\" star=\"\">\n",
                    "      <o base=\"string\" data=\"bytes\">6F 72 67 2F 65 6F 6C 61 6E 67 2F 53 6F 6D 65 49 6E 74 65 72 66 61 63 65</o>\n",
                    "   </o>\n",
                    "</o>\n"
                )
            )
        );
    }
}
