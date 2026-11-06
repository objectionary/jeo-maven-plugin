/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for an EO number.
 *
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
     *
     * @checkstyle MemberNameCheck (2 lines)
     */
    private final String as;

    /**
     * Comment to put inside the number.
     */
    private final Iterable<Directive> comment;

    /**
     * Constructor.
     *
     * @param hex Hex number
     */
    DirectivesNumber(final String hex) {
        this("", hex);
    }

    /**
     * Constructor.
     *
     * @param name Name of the number
     * @param hex Hex number
     */
    DirectivesNumber(final String name, final String hex) {
        this(name, hex, new Directives());
    }

    /**
     * Constructor.
     *
     * @param name Name of the number
     * @param hex Hex number
     * @param comment Comment to put inside the number
     */
    DirectivesNumber(final String name, final String hex, final Iterable<Directive> comment) {
        this(name, "", hex, comment);
    }

    /**
     * Constructor.
     *
     * @param name Name of the number
     * @param as The 'as' attribute of the object
     * @param hex Hex number
     * @param comment Comment to put inside the number
     * @checkstyle ParameterNameCheck (10 lines)
     */
    private DirectivesNumber(
        final String name,
        final String as,
        final String hex,
        final Iterable<Directive> comment
    ) {
        this.as = as;
        this.hex = hex;
        this.name = name;
        this.comment = comment;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesClosedObject(
            new EoFqn("number").fqn(),
            this.as,
            this.name,
            new Directives(this.comment).append(new DirectivesBytes(this.hex, "", "as-bytes"))
        ).iterator();
    }
}
