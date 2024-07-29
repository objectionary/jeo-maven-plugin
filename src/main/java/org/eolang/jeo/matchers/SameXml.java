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
package org.eolang.jeo.matchers;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.cactoos.Scalar;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Matcher to check if the received XML document is the same as the expected one.
 * Smart comparison of XML documents that ignores 'line' attributes.
 * @since 0.6
 */
public final class SameXml extends TypeSafeMatcher<String> {

    /**
     * Expected XML document.
     */
    private final String expected;

    /**
     * Constructor.
     * @param xml Expected XML document.
     */
    public SameXml(final XML xml) {
        this(xml.toString());
    }

    /**
     * Constructor.
     * @param expected Expected XML document.
     */
    public SameXml(final String expected) {
        this.expected = expected;
    }

    @Override
    public boolean matchesSafely(final String item) {
        return new WithoutLines(new XMLDocument(this.expected)).value()
            .equals(new WithoutLines(new XMLDocument(item)).value());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("XML documents is not the same.")
            .appendText("Expected:\n")
            .appendText(this.expected);
    }

    /**
     * The same XML, but without lines.
     * @since 0.3.1
     */
    public static final class WithoutLines implements Scalar<XML> {

        /**
         * The original.
         */
        private final XML original;

        /**
         * Constructor.
         * @param orgnl Original XML
         */
        public WithoutLines(final XML orgnl) {
            this.original = orgnl;
        }

        @Override
        public XML value() {
            try {
                return new XMLDocument(
                    new Xembler(
                        new Directives().xpath(".//o[@line]/@line").remove()
                    ).apply(this.original.node())
                );
            } catch (final ImpossibleModificationException exception) {
                throw new IllegalStateException(
                    "Failed to remove 'line' attributes", exception
                );
            }
        }
    }
}
