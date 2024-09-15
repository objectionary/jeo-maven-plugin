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
package org.eolang.jeo.matchers;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link WithoutLines}.
 *
 * @since 0.6
 */
final class WithoutLinesTest {

    @Test
    void transformsToXmlWithoutLines() {
        final XML lineless = new WithoutLines(
            new XMLDocument(
                String.join(
                    "",
                    "<root>",
                    "<o line=\"val\">data</o>",
                    "<anotherElement>",
                    "<o line=\"\">data</o>",
                    "</anotherElement>",
                    "</root>"
                )
            )
        ).value();
        final XML expected = new XMLDocument(
            String.join(
                "",
                "<root>",
                "<o>data</o>",
                "<anotherElement>",
                "<o>data</o>",
                "</anotherElement>",
                "</root>"
            )
        );
        MatcherAssert.assertThat(
            String.format(
                "XML transformed: '%s', but does not match with expected XML'%s'",
                lineless,
                expected
            ),
            lineless,
            new IsEqual<>(expected)
        );
    }
}
