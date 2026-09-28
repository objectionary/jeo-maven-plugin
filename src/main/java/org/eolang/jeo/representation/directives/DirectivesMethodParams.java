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
 *
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
     * Format of the directives.
     */
    private final Format format;

    /**
     * Number of parameters the visible annotations table has.
     */
    private final int visible;

    /**
     * Number of parameters the invisible annotations table has.
     */
    private final int invisible;

    /**
     * Constructor.
     */
    public DirectivesMethodParams() {
        this(new ArrayList<>(0));
    }

    /**
     * Constructor.
     *
     * @param params Parameters
     */
    @SafeVarargs
    public DirectivesMethodParams(final Iterable<Directive>... params) {
        this(Arrays.asList(params));
    }

    /**
     * Constructor.
     *
     * @param params Parameters
     */
    public DirectivesMethodParams(final List<Iterable<Directive>> params) {
        this(params, new ArrayList<>(0));
    }

    /**
     * Constructor.
     *
     * @param params Parameters
     * @param annotations Parameter annotations
     */
    public DirectivesMethodParams(
        final List<Iterable<Directive>> params,
        final List<Iterable<Directive>> annotations
    ) {
        this(params, annotations, new Format(), 0, 0);
    }

    /**
     * Constructor.
     *
     * @param params Parameters
     * @param annotations Parameter annotations
     * @param format Format of the directives
     * @param visible Number of parameters the visible annotations table has
     * @param invisible Number of parameters the invisible annotations table has
     */
    public DirectivesMethodParams(
        final List<Iterable<Directive>> params,
        final List<Iterable<Directive>> annotations,
        final Format format,
        final int visible,
        final int invisible
    ) {
        this.params = params;
        this.annotations = annotations;
        this.format = format;
        this.visible = visible;
        this.invisible = invisible;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "params",
            "params",
            Stream.concat(
                Stream.concat(
                    this.params.stream().map(Directives::new),
                    this.annotations.stream().map(Directives::new)
                ),
                this.counts()
            ).collect(Collectors.toList())
        ).iterator();
    }

    private Stream<Directives> counts() {
        final List<Directives> res = new ArrayList<>(2);
        if (this.visible > 0) {
            res.add(
                new Directives(
                    new DirectivesValue(this.format, "annotable-visible", this.visible)
                )
            );
        }
        if (this.invisible > 0) {
            res.add(
                new Directives(
                    new DirectivesValue(this.format, "annotable-invisible", this.invisible)
                )
            );
        }
        return res.stream();
    }
}
