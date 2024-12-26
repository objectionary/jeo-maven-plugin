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
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * Jcabi XML document.
 * @since 0.8
 */
public final class JcabiXmlDoc implements XmlDoc {

    /**
     * XML.
     */
    private final XmlNode doc;

    /**
     * Constructor.
     * @param doc XML
     */
    public JcabiXmlDoc(final XML doc) {
        this(new JcabiXmlNode(doc.inner().getFirstChild()));
    }

    /**
     * Constructor.
     * @param path Path to XML file.
     */
    public JcabiXmlDoc(final Path path) {
        this(JcabiXmlDoc.open(path));
    }

    /**
     * Constructor.
     * @param root Root node
     */
    private JcabiXmlDoc(final XmlNode root) {
        this.doc = root;
    }

    @Override
    public XmlNode root() {
        return this.doc;
    }

    @Override
    public void validate() {
        this.doc.validate();
    }

    /**
     * Convert a path to XML.
     * @param path Path to XML file.
     * @return XML.
     * @checkstyle IllegalCatchCheck (20 lines)
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private static XmlNode open(final Path path) {
        try {
            return new JcabiXmlNode(new XMLDocument(path).inner().getFirstChild());
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format("Can't find file '%s'", path),
                exception
            );
        } catch (final Exception broken) {
            throw new IllegalStateException(
                String.format(
                    "Can't parse XML from the file '%s'",
                    path
                ),
                broken
            );
        }
    }
}
