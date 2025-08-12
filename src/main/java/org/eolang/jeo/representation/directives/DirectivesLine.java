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
     * Line number in the source code.
     */
    private final int number;

    /**
     * Simple label identifier.
     */
    private final String identifier;

    /**
     * Constructor.
     * @param number Line number in the source code
     * @param identifier Identifier for the line
     */
    public DirectivesLine(final int number, final String identifier) {
        this.number = number;
        this.identifier = identifier;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "line-number",
            new RandName("ln").toString(),
            new DirectivesValue("number", this.number),
            new DirectivesLabel(this.identifier)
        ).iterator();
    }
}
