/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Pretty-prints XML content.
 * <p>This class takes an XML string or an XML object and formats it with proper indentation
 * and newlines for better readability.</p>
 * @since 0.11.0
 */
public final class PrettyXml {

    /**
     * Original XML content as a string.
     */
    private final String xml;

    /**
     * Constructor.
     * @param xml XML content as an XML object
     */
    PrettyXml(final XML xml) {
        this(xml.toString());
    }

    /**
     * Constructor.
     * @param xml XML content as a string
     */
    PrettyXml(final String xml) {
        this.xml = xml;
    }

    @Override
    public String toString() {
        try {
            final SAXReader reader = new SAXReader();
            final Document document = reader.read(new StringReader(this.xml));
            final OutputFormat format = OutputFormat.createCompactFormat();
            format.setNewlines(true);
            format.setIndent(true);
            format.setIndentSize(2);
            format.setTrimText(true);
            final StringWriter out = new StringWriter();
            final XMLWriter writer = new XMLWriter(out, format);
            writer.write(document);
            writer.close();
            return out.toString();
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Failed to format and write XML content: %n%s", this.xml),
                exception
            );
        } catch (final DocumentException exception) {
            throw new IllegalStateException(
                String.format("Failed to parse XML content: %n%s", this.xml),
                exception
            );
        }
    }
}
