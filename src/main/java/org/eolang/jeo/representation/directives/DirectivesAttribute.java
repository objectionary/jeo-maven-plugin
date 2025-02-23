/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for an attribute.
 * @since 0.4
 */
public final class DirectivesAttribute implements Iterable<Directive> {

    /**
     * The base of the attribute.
     */
    private final String base;

    /**
     * Data to store.
     */
    private final List<Iterable<Directive>> data;

    /**
     * Constructor.
     * @param base The name of the attribute.
     * @param data Properties of an attribute.
     */
    @SafeVarargs
    public DirectivesAttribute(final String base, final Iterable<Directive>... data) {
        this(base, Arrays.asList(data));
    }

    /**
     * Constructor.
     * @param base The base of the attribute.
     * @param data Properties of an attribute.
     */
    private DirectivesAttribute(final String base, final List<Iterable<Directive>> data) {
        this.base = base;
        this.data = data;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            this.base,
            this.data.stream().map(Directives::new).toArray(Directives[]::new)
        ).iterator();
    }
}
