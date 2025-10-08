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
     * The 'as' attribute of the object.
     * @checkstyle MemberNameCheck (2 lines)
     */
    private final String as;

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
        this(name, "", hex);
    }

    /**
     * Constructor.
     *
     * @param name Name of the number.
     * @param as The 'as' attribute of the object.
     * @param hex Hex number.
     * @checkstyle ParameterNameCheck (5 lines)
     */
    private DirectivesNumber(final String name, final String as, final String hex) {
        this.as = as;
        this.hex = hex;
        this.name = name;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesClosedObject(
            new EoFqn("number").fqn(),
            this.as,
            this.name,
            new DirectivesBytes(this.hex, "", "α0")
        ).iterator();
    }
}
