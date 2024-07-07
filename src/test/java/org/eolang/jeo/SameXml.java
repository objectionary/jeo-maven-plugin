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
package org.eolang.jeo;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Matcher to check if the received XML document is the same as the expected one.
 * Smart comparison of XML documents that ignores 'line' attributes.
 * @since 0.5
 */
@SuppressWarnings({
    "JTCOP.RuleAllTestsHaveProductionClass",
    "JTCOP.RuleCorrectTestName",
    "JTCOP.RuleInheritanceInTests"
})
public final class SameXml extends TypeSafeMatcher<String> {

    /**
     * Expected XML document.
     */
    private final String expected;

    /**
     * Constructor.
     * @param expected Expected XML document.
     */
    public SameXml(final String expected) {
        this.expected = expected;
    }

    @Override
    protected boolean matchesSafely(final String item) {
        return SameXml.withoutLines(new XMLDocument(this.expected))
            .equals(SameXml.withoutLines(new XMLDocument(item)));
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("XML documents is not the same.")
            .appendText("Expected:\n")
            .appendText(this.expected);
    }

    /**
     * Remove 'line' attributes from the XML document.
     * @param original Original XML document.
     * @return XML document without 'line' attributes.
     */
    private static XML withoutLines(final XML original) {
        try {
            return new XMLDocument(
                new Xembler(
                    new Directives().xpath(".//o[@line]/@line").remove()
                ).apply(original.node())
            );
        } catch (final ImpossibleModificationException exception) {
            throw new IllegalStateException("Failed to remove 'line' attributes", exception);
        }
    }

}