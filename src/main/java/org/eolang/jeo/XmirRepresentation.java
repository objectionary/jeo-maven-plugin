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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Intermediate representation of a class files from XMIR.
 *
 * @since 0.1.0
 * @todo #39:90min Add unit test for XmirIR class.
 *  The test should check all the methods of the {@link XmirRepresentation} class.
 *  Don't forget to test corner cases.
 *  When the test is ready, remove this puzzle.
 */
public final class XmirRepresentation implements Representation {

    /**
     * XML.
     */
    private final XML xml;

    /**
     * Constructor.
     * @param xml XML.
     */
    XmirRepresentation(final XML xml) {
        this.xml = XmirRepresentation.xmir(xml);
    }

    @Override
    public String name() {
        return this.xml.xpath("/program/@name").get(0);
    }

    @Override
    public XML toEO() {
        return this.xml;
    }

    @Override
    public byte[] toBytecode() {
        return Base64.getDecoder().decode(this.xml.xpath("/program/listing/text()").get(0));
    }

    /**
     * Fake XMIR.
     * @param name Name of an object.
     * @return XMIR.
     */
    static XML fake(final String name) {
        try {
            final String now = ZonedDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);
            return new XMLDocument(
                new Xembler(
                    new Directives()
                        .add("program")
                        .attr("name", name)
                        .attr("version", "0.0.0")
                        .attr("revision", "0.0.0")
                        .attr("dob", now)
                        .attr("time", now)
                        .add("listing").up()
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
     * Validate XMIR.
     * @param xml XML.
     * @return XMIR.
     */
    private static XML xmir(final XML xml) {
        new Schema(xml).check();
        return xml;
    }
}
