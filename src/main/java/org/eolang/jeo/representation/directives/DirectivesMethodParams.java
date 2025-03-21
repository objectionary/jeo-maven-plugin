/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
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
     * Constructor.
     */
    public DirectivesMethodParams() {
        this(new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param params Parameters.
     */
    public DirectivesMethodParams(final List<Iterable<Directive>> params) {
        this.params = params;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesOptionalJeoObject(
            "params",
            "params",
            this.params.stream().map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }
}
