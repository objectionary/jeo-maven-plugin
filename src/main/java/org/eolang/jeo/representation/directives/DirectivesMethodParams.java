/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for method parameters.
 * @since 0.1
 */
public final class DirectivesMethodParams implements Iterable<Directive> {

    /**
     * Parameters.
     */
    private final List<Iterable<Directive>> params;

    /**
     * Parameter annotations.
     */
    private final List<Iterable<Directive>> annotations;

    /**
     * Constructor.
     */
    public DirectivesMethodParams() {
        this(new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param params Parameters.
     */
    @SafeVarargs
    public DirectivesMethodParams(final Iterable<Directive>... params) {
        this(Arrays.asList(params));
    }

    /**
     * Constructor.
     * @param params Parameters.
     */
    public DirectivesMethodParams(final List<Iterable<Directive>> params) {
        this(params, new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param params Parameters.
     * @param annotations Parameter annotations.
     */
    public DirectivesMethodParams(
        final List<Iterable<Directive>> params,
        final List<Iterable<Directive>> annotations
    ) {
        this.params = params;
        this.annotations = annotations;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "params",
            "params",
            Stream.concat(
                this.params.stream().map(Directives::new),
                this.annotations.stream().map(Directives::new)
            ).collect(Collectors.toList())
        ).iterator();
    }
}
