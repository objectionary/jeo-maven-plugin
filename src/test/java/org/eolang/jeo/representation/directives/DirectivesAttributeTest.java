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
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesAttribute}.
 * @since 0.6
 */
final class DirectivesAttributeTest {

    @Test
    void convertsToXmirWithoutChildren() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the attribute will be converted to Xmir as a simple object without children",
            new Xembler(new DirectivesAttribute("some")).xml(),
            XhtmlMatchers.hasXPaths("./o[contains(@base, 'some')]")
        );
    }

    @Test
    void convertsToXmirWithChildren() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that the attribute will be converted to Xmir with children",
            new Xembler(
                new DirectivesAttribute(
                    "children",
                    new DirectivesData("data")
                )
            ).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[contains(@base, 'children')]",
                "./o[contains(@base, 'children')]/o[contains(@base, 'string')]"
            )
        );
    }
}
