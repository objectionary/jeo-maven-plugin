/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.directives.DirectivesObject;
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
    private final XML xmir;

    /**
     * Constructor.
     * @param directives Directives to build the EO program from
     */
    MeasuredEo(final DirectivesObject directives) {
        this(new TwoSpaceXml(new XMLDocument(new Xembler(directives).xmlQuietly())));
    }

    /**
     * Constructor.
     * @param xmir Original XMIR representation
     */
    private MeasuredEo(final XML xmir) {
        this.xmir = xmir;
    }

    /**
     * Get XML representation of the EO with timing information.
     * @return XML representation with embedded timing metadata and two-space indentation
     * @throws ImpossibleModificationException If XMIR modification fails
     */
    XML asXml() throws ImpossibleModificationException {
        final long start = System.currentTimeMillis();
        final long end = System.currentTimeMillis();
        return new TwoSpaceXml(
            new XMLDocument(
                new Xembler(
                    new Directives()
                        .xpath("/program[@ms]/@ms")
                        .set(String.format("%d", end - start))
                ).apply(this.xmir.deepCopy())
            )
        );
    }
}
