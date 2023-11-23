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
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.w3c.dom.Node;

/**
 * XMIR Program.
 * @since 0.1
 * @todo #174:90min Add unit tests for XmlProgram.
 *  Currently we don't have unit tests for XmlProgram. So, it makes sense to add
 *  them to keep code safe and clear.
 */
public class XmlProgram {

    /**
     * Program node name.
     */
    private static final String PROGRAM = "program";

    /**
     * Root node.
     */
    private final Node root;

    /**
     * Constructor.
     * @param xml Raw XMIR.
     */
    public XmlProgram(final XML xml) {
        this(xml.node());
    }

    /**
     * Constructor.
     * @param root Root node.
     */
    private XmlProgram(final Node root) {
        this.root = root;
    }

    /**
     * Create a copy of this program.
     * @return Copy of this program.
     */
    public XmlProgram copy() {
        return new XmlProgram(this.root);
    }

    /**
     * Find top-level class.
     * @return Class.
     */
    public XmlClass top() {
        return new XmlNode(this.root)
            .child(XmlProgram.PROGRAM)
            .child("objects")
            .child("o")
            .toClass();
    }

    /**
     * Set top-level class and return new XmlProgram.
     * @param clazz Class.
     * @return New XmlProgram.
     */
    public XmlProgram with(final XmlClass clazz) {
        final Node res = new XMLDocument(this.root).node();
        new XmlNode(res)
            .child(XmlProgram.PROGRAM)
            .child("objects")
            .clean()
            .append(clazz.node());
        return new XmlProgram(res);
    }

    /**
     * Retrieve program package.
     * @return Package.
     */
    public String pckg() {
        return new XmlNode(this.root)
            .child(XmlProgram.PROGRAM)
            .child("metas")
            .child("meta")
            .child("tail")
            .text();
    }

    /**
     * Convert to XMIR .
     * @return XMIR.
     */
    public XML toXmir() {
        return new XMLDocument(this.root);
    }

    /**
     * Set program name.
     * @param name Name.
     * @return The same program.
     */
    public XmlProgram withName(final String name) {
        new XmlNode(this.root)
            .child(XmlProgram.PROGRAM)
            .withAttribute("name", name);
        return this;
    }

    /**
     * Set program time.
     * @param time Time.
     * @return The same program.
     */
    public XmlProgram withTime(final LocalDateTime time) {
        new XmlNode(this.root)
            .child(XmlProgram.PROGRAM)
            .withAttribute("time", time.format(DateTimeFormatter.ISO_DATE_TIME));
        return this;
    }

    /**
     * Set listing.
     * @param listing Listing.
     * @return The same program.
     */
    public XmlProgram withListing(final String listing) {
        new XmlNode(this.root)
            .child(XmlProgram.PROGRAM)
            .child("listing")
            .withText(listing);
        return this;
    }
}
