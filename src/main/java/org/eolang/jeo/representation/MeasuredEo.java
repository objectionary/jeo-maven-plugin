/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * A utility class that measures the time taken to convert bytecode to XMIR.
 *
 * <p>This class wraps the transformation process and adds timing metadata to the
 * resulting XMIR program. The timing information is embedded in the program's
 * ms attribute.</p>
 * @since 0.6.0
 */
final class MeasuredEo {

    /**
     * Original transformation.
     */
    private final Iterable<Directive> directives;

    /**
     * Constructor.
     * @param directives Directives to build the EO program from
     */
    MeasuredEo(final Iterable<Directive> directives) {
        this.directives = directives;
    }

    /**
     * Get XML representation of the EO with timing information.
     * @return XML representation with embedded timing metadata
     * @throws ImpossibleModificationException If XMIR modification fails
     */
    XML asXml() throws ImpossibleModificationException {
        final long start = System.currentTimeMillis();
        final XML doc = new XMLDocument(new Xembler(this.directives).xmlQuietly());
        final long end = System.currentTimeMillis();
        return new XMLDocument(
            new Xembler(
                new Directives()
                    .xpath("/program[@ms]/@ms")
                    .set(String.format("%d", end - start))
            ).apply(doc.inner())
        );
    }
}
