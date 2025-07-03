/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

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
     * Name of the number.
     */
    private final String name;

    /**
     * Constructor.
     * @param hex Hex number.
     */
    DirectivesNumber(final String hex) {
        this("", hex);
    }

    /**
     * Constructor.
     *
     * @param name Name of the number.
     * @param hex Hex number.
     */
    DirectivesNumber(final String name, final String hex) {
        this.hex = hex;
        this.name = name;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesClosedObject(
            new EoFqn("number").fqn(),
            "",
            this.name,
            new DirectivesBytes(this.hex)
        ).iterator();
    }
}
