/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * Jcabi XML document.
 *
 * @since 0.8
 */
public final class JcabiXmlDoc implements XmlDoc {

    /**
     * XML.
     */
    private final XmlNode doc;

    /**
     * Source description (file path or placeholder).
     */
    private final String source;

    /**
     * Constructor.
     *
     * @param path Path to XML file
     */
    public JcabiXmlDoc(final Path path) {
        this(new JcabiXmlNode(JcabiXmlDoc.open(path)), path.toString());
    }

    /**
     * Constructor.
     *
     * @param doc XML
     */
    public JcabiXmlDoc(final XML doc) {
        this(new JcabiXmlNode(doc), "Unknown");
    }

    /**
     * Constructor.
     *
     * @param root Root node
     * @param source Source description
     */
    private JcabiXmlDoc(final XmlNode root, final String source) {
        this.doc = root;
        this.source = source;
    }

    @Override
    public XmlNode root() {
        return this.doc.child("object");
    }

    @Override
    public void validate() {
        try {
            this.doc.validate();
        } catch (final IllegalStateException exception) {
            throw new IllegalStateException(
                String.format("Invalid XMIR in '%s': %s", this.source, exception.getMessage()),
                exception
            );
        } catch (final IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                String.format("Invalid XMIR in '%s': %s", this.source, exception.getMessage()),
                exception
            );
        }
    }

    private static XML open(final Path path) {
        try {
            return new XMLDocument(path);
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format("Can't find file '%s'", path),
                exception
            );
        } catch (final IllegalArgumentException broken) {
            throw new IllegalStateException(
                String.format(
                    "Can't parse Jcabi XML from the file '%s'",
                    path
                ),
                broken
            );
        }
    }
}
