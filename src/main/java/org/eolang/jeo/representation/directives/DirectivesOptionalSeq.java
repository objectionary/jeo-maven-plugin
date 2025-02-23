/*
 * The MIT License (MIT)
 *
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
 * Otherwise, it returns the sequence of directives.
 *
 * @since 0.8
 */
final class DirectivesOptionalSeq implements Iterable<Directive> {

    /**
     * Name of the sequence.
     */
    private final String name;

    /**
     * Original sequence.
     */
    private final List<? extends Iterable<Directive>> original;

    /**
     * Constructor.
     * @param name Name of the sequence.
     * @param elements Elements.
     */
    DirectivesOptionalSeq(
        final String name,
        final List<? extends Iterable<Directive>> elements
    ) {
        this.name = name;
        this.original = elements;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterator<Directive> result;
        if (this.original.isEmpty()) {
            result = new Directives().iterator();
        } else {
            result = new DirectivesSeq(this.name, this.original).iterator();
        }
        return result;
    }
}
