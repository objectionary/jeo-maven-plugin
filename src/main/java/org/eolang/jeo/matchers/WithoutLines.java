/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.matchers;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.cactoos.Scalar;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * The same XML, but without lines and comments.
 *
 * @since 0.6
 */
public final class WithoutLines implements Scalar<XML> {

    /**
     * The original.
     */
    private final XML original;

    /**
     * Constructor.
     * @param orgnl Original XML
     */
    public WithoutLines(final XML orgnl) {
        this.original = orgnl;
    }

    @Override
    public XML value() {
        try {
            return new XMLDocument(
                new Xembler(
                    new Directives()
                        .xpath(".//o[@line]/@line").remove()
                        .xpath("//comment()").remove()
                ).apply(this.original.inner())
            );
        } catch (final ImpossibleModificationException exception) {
            throw new IllegalStateException(
                "Failed to remove 'line' attributes", exception
            );
        }
    }
}
