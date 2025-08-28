/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

/**
 * Line number directives.
 * @since 0.14.0
 */
public final class DirectivesLine implements Iterable<Directive> {

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * Line number in the source code.
     */
    private final int number;

    /**
     * Simple label identifier.
     */
    private final String identifier;

    /**
     * Constructor.
     * @param format Format of the directives
     * @param number Line number in the source code
     * @param identifier Identifier for the line
     */
    public DirectivesLine(final Format format, final int number, final String identifier) {
        this.format = format;
        this.number = number;
        this.identifier = identifier;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "line-number",
            new RandName("ln").toString(),
            new DirectivesValue(this.format, "number", this.number),
            new DirectivesLabel(this.format, this.identifier)
        ).iterator();
    }
}
