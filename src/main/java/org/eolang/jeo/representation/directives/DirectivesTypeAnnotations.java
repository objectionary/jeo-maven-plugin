/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;

/**
 * Directives Type Annotations.
 * @since 0.15.0
 */
public final class DirectivesTypeAnnotations implements Iterable<Directive> {

    /**
     * Annotations name.
     */
    private final String name;

    /**
     * All the annotations.
     */
    private final List<Iterable<Directive>> annotations;

    /**
     * Constructor.
     * @param annotations Annotations.
     */
    @SafeVarargs
    public DirectivesTypeAnnotations(final Iterable<Directive>... annotations) {
        this(Arrays.asList(annotations));
    }

    /**
     * Constructor.
     * @param annotations Annotations.
     */
    public DirectivesTypeAnnotations(final List<Iterable<Directive>> annotations) {
        this("type-annotations", annotations);
    }

    /**
     * Constructor.
     * @param name Name.
     * @param annotations Annotations.
     */
    private DirectivesTypeAnnotations(
        final String name, final List<Iterable<Directive>> annotations) {
        this.name = name;
        this.annotations = annotations;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesSeq(
            this.name,
            this.annotations
        ).iterator();
    }
}
