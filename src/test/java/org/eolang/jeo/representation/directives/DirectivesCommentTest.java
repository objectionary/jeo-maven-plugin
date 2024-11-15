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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesComment}.
 * @since 0.6
 */
final class DirectivesCommentTest {

    @Test
    void createsComment() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't create proper xml a comment",
            new Xembler(
                new Directives().append(new DirectivesComment("Hello, world!"))
            ).xml(),
            Matchers.containsString("<!-- Hello, world! -->")
        );
    }

    @Test
    void escapesUnsafeCharacters() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't escape unsafe characters",
            new Xembler(
                new Directives().append(new DirectivesComment("Hello -- <world> ---!"))
            ).xml(),
            Matchers.containsString("<!-- Hello &#45;&#45; &lt;world&gt; &#45;&#45;&#45;! -->")
        );
    }
}
