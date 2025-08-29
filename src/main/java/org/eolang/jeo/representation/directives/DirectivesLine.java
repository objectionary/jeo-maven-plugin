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
     * Line index.
     */
    private final int index;

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
     * @param index Line index
     * @param format Format of the directives
     * @param number Line number in the source code
     * @param identifier Identifier for the line
     * @checkstyle ParameterNumber (5 lines)
     */
    public DirectivesLine(
        final int index,
        final Format format,
        final int number,
        final String identifier
    ) {
        this.index = index;
        this.format = format;
        this.number = number;
        this.identifier = identifier;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "line-number",
            new NumName("ln", this.index).toString(),
            new DirectivesValue(this.format, "number", this.number),
            new DirectivesLabel(0, this.format, this.identifier)
        ).iterator();
    }
}
