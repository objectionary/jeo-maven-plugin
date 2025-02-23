/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
     * @param path Path to XML file.
     */
    public JcabiXmlDoc(final Path path) {
        this(JcabiXmlDoc.open(path));
    }

    /**
     * Constructor.
     * @param doc XML
     */
    public JcabiXmlDoc(final XML doc) {
        this(new JcabiXmlNode(doc));
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
        return this.doc.child("program");
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
    private static XML open(final Path path) {
        try {
            return new XMLDocument(path);
        } catch (final FileNotFoundException exception) {
            throw new IllegalStateException(
                String.format("Can't find file '%s'", path),
                exception
            );
        } catch (final RuntimeException broken) {
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
