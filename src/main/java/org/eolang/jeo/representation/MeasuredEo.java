/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xembly.Directive;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * A utility class that measures the time taken to convert bytecode to XMIR.
 *
 * <p>This class wraps the transformation process and adds timing metadata to the resulting XMIR
 * program. The timing information is embedded in the program's ms attribute.</p>
 *
 * @since 0.6.0
 */
final class MeasuredEo {

    /**
     * Original transformation.
     */
    private final Iterable<Directive> directives;

    /**
     * Constructor.
     *
     * @param directives Directives to build the EO program from
     */
    MeasuredEo(final Iterable<Directive> directives) {
        this.directives = directives;
    }

    /**
     * Get XML representation of the EO with timing information.
     *
     * <p>The DOM document is built once and returned directly, without serializing it
     * into a string and parsing that string back. For large classes this substantially
     * reduces the peak memory consumption, see
     * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1875">#1875</a>.</p>
     *
     * @return XML representation with embedded timing metadata
     * @throws ImpossibleModificationException If XMIR modification fails
     */
    XML asXml() throws ImpossibleModificationException {
        final long start = System.currentTimeMillis();
        final Document dom = new Xembler(this.directives).dom();
        final long end = System.currentTimeMillis();
        final Element root = dom.getDocumentElement();
        if (root != null && root.hasAttribute("ms")) {
            root.setAttribute("ms", String.valueOf(end - start));
        }
        return new XMLDocument(dom);
    }
}
