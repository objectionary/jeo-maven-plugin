/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;

/**
 * XML with two-space indentation.
 * This class wraps an XML object and reformats its string representation
 * to use two-space indentation instead of the default three-space indentation.
 *
 * @since 0.6.0
 */
public final class TwoSpaceXml implements XML {

    /**
     * Original XML.
     */
    private final XML original;

    /**
     * Constructor.
     * @param xml Original XML with any indentation
     */
    public TwoSpaceXml(final XML xml) {
        this.original = xml;
    }

    @Override
    public String toString() {
        String result = this.original.toString();
        // Convert 3-space based indentation to 2-space based indentation
        // Process each line individually to avoid pattern conflicts
        StringBuilder output = new StringBuilder();
        String[] lines = result.split("\n", -1);
        
        for (String line : lines) {
            // Count leading spaces
            int leadingSpaces = 0;
            for (char c : line.toCharArray()) {
                if (c == ' ') {
                    leadingSpaces++;
                } else {
                    break;
                }
            }
            
            // Convert 3-space indentation to 2-space indentation
            if (leadingSpaces > 0 && leadingSpaces % 3 == 0) {
                int levels = leadingSpaces / 3;
                String newIndent = new String(new char[levels * 2]).replace('\0', ' ');
                String lineContent = line.substring(leadingSpaces);
                output.append(newIndent).append(lineContent);
            } else {
                output.append(line);
            }
            
            output.append('\n');
        }
        
        // Remove the last newline if the original didn't end with one
        if (!result.endsWith("\n") && output.length() > 0) {
            output.setLength(output.length() - 1);
        }
        
        return output.toString();
    }

    @Override
    public java.util.List<String> xpath(final String xpath) {
        return this.original.xpath(xpath);
    }

    @Override
    public java.util.List<XML> nodes(final String xpath) {
        return this.original.nodes(xpath);
    }

    @Override
    public XML registerNs(final String prefix, final Object uri) {
        return new TwoSpaceXml(this.original.registerNs(prefix, uri));
    }

    @Override
    public XML merge(final javax.xml.namespace.NamespaceContext context) {
        return new TwoSpaceXml(this.original.merge(context));
    }

    @Override
    @SuppressWarnings("deprecation")
    public org.w3c.dom.Node node() {
        return this.original.node();
    }

    @Override
    public org.w3c.dom.Node inner() {
        return this.original.inner();
    }

    @Override
    public org.w3c.dom.Node deepCopy() {
        return this.original.deepCopy();
    }

    @Override
    public java.util.Collection<org.xml.sax.SAXParseException> validate(
        final org.w3c.dom.ls.LSResourceResolver resolver
    ) {
        return this.original.validate(resolver);
    }

    @Override
    public java.util.Collection<org.xml.sax.SAXParseException> validate(final XML xsd) {
        return this.original.validate(xsd);
    }
}