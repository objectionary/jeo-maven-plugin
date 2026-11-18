/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Pretty-prints XML content.
 *
 * <p>This class takes an XML string or an XML object and formats it with proper indentation and
 * newlines for better readability.</p>
 *
 * @since 0.11.0
 */
public final class PrettyXml {

    /**
     * Original XML content as a string.
     */
    private final String xml;

    /**
     * Constructor.
     *
     * @param xml XML content as an XML object
     */
    PrettyXml(final XML xml) {
        this(xml.toString());
    }

    /**
     * Constructor.
     *
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
            final Map<String, String> listings = new HashMap<>(0);
            if (document.getRootElement() != null) {
                PrettyXml.stashListings(document.getRootElement(), listings, this.xml);
            }
            final OutputFormat format = OutputFormat.createCompactFormat();
            format.setNewlines(true);
            format.setIndent(true);
            format.setIndentSize(2);
            format.setTrimText(true);
            final StringWriter out = new StringWriter();
            final XMLWriter writer = new XMLWriter(out, format);
            writer.write(document);
            writer.close();
            return PrettyXml.restoreListings(out.toString(), listings);
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

    private static void stashListings(
        final Element element,
        final Map<String, String> listings,
        final String raw
    ) throws IOException {
        if ("listing".equals(element.getName()) && !element.content().isEmpty()) {
            final StringWriter buf = new StringWriter();
            final OutputFormat preserve = OutputFormat.createCompactFormat();
            preserve.setTrimText(false);
            final XMLWriter writer = new XMLWriter(buf, preserve);
            for (final Node child : element.content()) {
                writer.write(child);
            }
            writer.close();
            String token = String.format("jeo-listing-%d", listings.size());
            while (raw.contains(token) || listings.containsKey(token)) {
                token = String.format("jeo-listing-%d-%d", listings.size(), token.length());
            }
            listings.put(token, buf.toString());
            element.setText(token);
        } else {
            for (final Element child : element.elements()) {
                PrettyXml.stashListings(child, listings, raw);
            }
        }
    }

    private static String restoreListings(
        final String formatted,
        final Map<String, String> listings
    ) {
        String result = formatted;
        for (final Map.Entry<String, String> entry : listings.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
