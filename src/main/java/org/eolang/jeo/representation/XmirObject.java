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
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * XMIR representation of an EO object.
 *
 * @since 0.1.0
 */
public final class XmirObject {

    /**
     * Object name.
     */
    private final String name;

    /**
     * Constructor.
     * @param name Object name.
     */
    public XmirObject(final String name) {
        this.name = name;
    }

    /**
     * Convert to XML.
     * @return XML representation of XMIR.
     */
    public XML xml() {
        try {
            final String now = ZonedDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);
            return new XMLDocument(
                new Xembler(
                    new Directives()
                        .add("program")
                        .attr("name", this.name)
                        .attr("version", "0.0.0")
                        .attr("revision", "0.0.0")
                        .attr("dob", now)
                        .attr("time", now)
                        .add("listing").set(XmirObject.mockListing()).up()
                        .add("errors").up()
                        .add("sheets").up()
                        .add("license").up()
                        .add("metas").up()
                        .attr("ms", System.currentTimeMillis())
                        .add("objects")
                        .add("o")
                        .attr("name", "test")
                ).xml()
            );
        } catch (final ImpossibleModificationException exception) {
            throw new IllegalStateException("Can't create fake XML", exception);
        }
    }

    /**
     * Bytecode listing in Base64.
     * @return Base64 listing.
     */
    private static String mockListing() {
        return String.join(
            "",
            "yv66vgAAADQAIgoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCQAIAAk",
            "HAAoMAAsADAEAEGphdmEvbGFuZy9TeXN0ZW0BAANvdXQBABVMamF2YS9pby9QcmludFN0cmVhbTsIAA4BAA1",
            "IZWxsbywgV29ybGQhCgAQABEHABIMABMAFAEAE2phdmEvaW8vUHJpbnRTdHJlYW0BAAdwcmludGxuAQAVKEx",
            "qYXZhL2xhbmcvU3RyaW5nOylWBwAWAQAab3JnL2VvbGFuZy9qZW8vQXBwbGljYXRpb24BAARDb2RlAQAPTGl",
            "uZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAHExvcmcvZW9sYW5nL2plby9BcHB",
            "saWNhdGlvbjsBAARtYWluAQAWKFtMamF2YS9sYW5nL1N0cmluZzspVgEABGFyZ3MBABNbTGphdmEvbGFuZy9",
            "TdHJpbmc7AQAKU291cmNlRmlsZQEAEEFwcGxpY2F0aW9uLmphdmEAIQAVAAIAAAAAAAIAAQAFAAYAAQAXAAA",
            "ALwABAAEAAAAFKrcAAbEAAAACABgAAAAGAAEAAAADABkAAAAMAAEAAAAFABoAGwAAAAkAHAAdAAEAFwAAADc",
            "AAgABAAAACbIABxINtgAPsQAAAAIAGAAAAAoAAgAAAAUACAAGABkAAAAMAAEAAAAJAB4AHwAAAAEAIAAAAAI",
            "AIQ=="
        );
    }
}
