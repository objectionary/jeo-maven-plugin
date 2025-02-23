/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Objects;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives with optional values.
 * If the values are empty, it returns nothing.
 * In opposite, {@link DirectivesValues}, in this case, returns an empty sequence.
 *
 * @since 0.8
 */
final class DirectivesOptionalValues implements Iterable<Directive> {

    /**
     * Tuple name.
     */
    private final String name;

    /**
     * Tuple values.
     */
    private final Object[] values;

    /**
     * Constructor.
     * @param name Name of the tuple.
     * @param values Values.
     */
    @SafeVarargs
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    DirectivesOptionalValues(final String name, final Object... values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterator<Directive> result;
        if (Objects.isNull(this.values) || this.values.length == 0) {
            result = new Directives().iterator();
        } else {
            result = new DirectivesValues(this.name, this.values).iterator();
        }
        return result;
    }
}
