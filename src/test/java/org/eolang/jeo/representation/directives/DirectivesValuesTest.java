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
package org.eolang.jeo.representation.directives;

import com.jcabi.matchers.XhtmlMatchers;
import org.eolang.jeo.representation.xmir.NativeXmlNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link DirectivesValues}.
 * @since 0.6
 */
final class DirectivesValuesTest {

    @Test
    void convertsToXmir() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "We expect that array of values will be converted to correct Xembly directives",
            new Xembler(new DirectivesValues("name", "value")).xml(),
            XhtmlMatchers.hasXPaths(
                "./o[contains(@base,'seq.of1') and contains(@as,'name')]",
                "./o[contains(@base,'seq.of1') and contains(@as,'name')]/o/o[contains(@base, 'org.eolang.bytes') and text()='76-61-6C-75-65']"
            )
        );
    }

    @RepeatedTest(100)
    void generatesRandomNameWithoutFirstDigit() {
        MatcherAssert.assertThat(
            "We expect that the name of the sequence will be generated randomly and will not start with a digit",
            new NativeXmlNode(
                new Xembler(
                    new DirectivesValues("", "some-value")
                ).xmlQuietly()
            ).attribute("as").orElseThrow(
                () -> new IllegalStateException("Name attribute is absent")
            ),
            Matchers.matchesRegex("^[^0-9].*")
        );
    }
}
