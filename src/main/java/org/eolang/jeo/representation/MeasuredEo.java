/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.directives.DirectivesProgram;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * This class tracks the time it takes to convert a bytecode to a XMIR program.
 * @since 0.6
 */
final class MeasuredEo {

    /**
     * Original transformation.
     */
    private final XML xmir;

    /**
     * Ctor.
     * @param directives Directives to build the EO from.
     */
    MeasuredEo(final DirectivesProgram directives) {
        this(new XMLDocument(new Xembler(directives).xmlQuietly()));
    }

    /**
     * Ctor.
     * @param xmir Original xmir representation.
     */
    private MeasuredEo(final XML xmir) {
        this.xmir = xmir;
    }

    /**
     * XML representation of the EO.
     * @return XML representation
     * @throws ImpossibleModificationException If something goes wrong
     */
    XML asXml() throws ImpossibleModificationException {
        final long start = System.currentTimeMillis();
        final long end = System.currentTimeMillis();
        return new XMLDocument(
            new Xembler(
                new Directives()
                    .xpath("/program[@ms]/@ms")
                    .set(String.format("%d", end - start))
            ).apply(this.xmir.deepCopy())
        );
    }
}
