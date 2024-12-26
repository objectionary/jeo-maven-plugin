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

import java.io.FileNotFoundException;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Synced;
import org.cactoos.scalar.Unchecked;

/**
 * Native XML document.
 * @since 0.7
 */
public final class NativeXmlDoc implements XmlDoc {

    /**
     * XML document factory.
     */
    private static final DocumentBuilderFactory DOC_FACTORY = DocumentBuilderFactory.newInstance();

    /**
     * XML.
     */
    private final Unchecked<XmlNode> xml;

    /**
     * Source of the XML.
     */
    private final String source;

    /**
     * Constructor.
     * @param path Path to XML file.
     */
    public NativeXmlDoc(final Path path) {
        this(fromFile(path), path.toAbsolutePath().toString());
    }

    private NativeXmlDoc(final Unchecked<XmlNode> xml, final String source) {
        this.xml = xml;
        this.source = source;
    }

    @Override
    public XmlNode root() {
        return this.xml.value();
    }

    /**
     * Prestructor that converts a path to a lazy XML.
     * @param path Path to an XML file.
     * @return Lazy XML.
     */
    private static Unchecked<XmlNode> fromFile(final Path path) {
        return new Unchecked<>(new Synced<>(new Sticky<>(() -> NativeXmlDoc.open(path))));
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
            return new NativeXmlNode(
                NativeXmlDoc.DOC_FACTORY
                    .newDocumentBuilder()
                    .parse(path.toFile())
                    .getDocumentElement()
            );
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
