/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Simple EO delegate object.
 * <p>
 *     Trivial object that represents a simple EO delegate object
 *     with an attribute 'base' and a name '@'.
 * </p>
 * @since 0.12.0
 */
public final class DirectivesSimpleDelegate implements Iterable<Directive> {

    /**
     * Base of the object.
     */
    private final String base;

    /**
     * Constructor.
     * @param base The 'base' attribute of the abstract object.
     */
    public DirectivesSimpleDelegate(final String base) {
        this.base = base;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives().add("o")
            .attr("base", this.base)
            .attr("name", "φ")
            .up()
            .iterator();
    }
}
