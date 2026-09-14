/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Synced;
import org.cactoos.scalar.Unchecked;
import org.xml.sax.SAXException;

/**
 * Native XML document.
 *
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
     * Constructor.
     *
     * @param path Path to XML file
     */
    public NativeXmlDoc(final Path path) {
        this(NativeXmlDoc.fromFile(path));
    }

    private NativeXmlDoc(final Unchecked<XmlNode> xml) {
        this.xml = xml;
    }

    @Override
    public XmlNode root() {
        return this.xml.value();
    }

    @Override
    public void validate() {
        this.xml.value().validate();
    }

    private static Unchecked<XmlNode> fromFile(final Path path) {
        return new Unchecked<>(new Synced<>(new Sticky<>(() -> NativeXmlDoc.open(path))));
    }

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
        } catch (final IOException | SAXException | ParserConfigurationException broken) {
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
