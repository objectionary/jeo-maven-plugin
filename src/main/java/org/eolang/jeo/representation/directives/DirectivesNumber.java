/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for an EO number.
 * @since 0.8
 */
final class DirectivesNumber implements Iterable<Directive> {

    /**
     * Hex value.
     */
    private final String hex;

    /**
     * Constructor.
     * @param hex Hex number.
     */
    DirectivesNumber(final String hex) {
        this.hex = hex;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .add("o")
            .attr("base", new EoFqn("number").fqn())
            .append(new DirectivesBytes(this.hex))
            .up()
            .iterator();
    }
}
