/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Optional directives sequence.
 * If the original sequence is empty, it returns nothing.
 * In opposite, {@link DirectivesSeq}, in this case, returns an empty sequence.
 *
 * @since 0.8
 */
final class DirectivesOptionalJeoObject implements Iterable<Directive> {

    /**
     * The base of the object.
     */
    private final String base;

    /**
     * The name of the object.
     */
    private final String name;

    /**
     * Inner components.
     */
    private final List<Directives> inner;

    /**
     * Constructor.
     * @param base The base of the object.
     * @param name The name of the object.
     * @param inner Inner components.
     */
    DirectivesOptionalJeoObject(
        final String base, final String name, final List<Directives> inner
    ) {
        this.base = base;
        this.name = name;
        this.inner = inner;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterator<Directive> result;
        if (this.inner.isEmpty()) {
            result = new Directives().iterator();
        } else {
            result = new DirectivesJeoObject(this.base, this.name, this.inner).iterator();
        }
        return result;
    }
}
