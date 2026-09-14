/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.xembly.Directive;

/**
 * Directives Annotation.
 *
 * @since 0.1
 */
public final class DirectivesAnnotations implements Iterable<Directive> {

    /**
     * All the annotations.
     */
    private final List<Iterable<Directive>> annotations;

    /**
     * Annotations name.
     */
    private final String name;

    /**
     * Constructor.
     */
    public DirectivesAnnotations() {
        this(new ArrayList<>(0));
    }

    /**
     * Constructor.
     *
     * @param annotations Annotations
     */
    public DirectivesAnnotations(final List<Iterable<Directive>> annotations) {
        this(annotations, "annotations");
    }

    /**
     * Constructor.
     *
     * @param name Name
     */
    DirectivesAnnotations(final String name) {
        this(new ArrayList<>(0), name);
    }

    /**
     * Constructor.
     *
     * @param name Name
     * @param annotations Annotations
     */
    DirectivesAnnotations(final String name, final DirectivesAnnotation... annotations) {
        this(Arrays.asList(annotations), name);
    }

    /**
     * Constructor.
     *
     * @param annotations Annotations
     * @param name Name
     */
    public DirectivesAnnotations(final List<Iterable<Directive>> annotations, final String name) {
        this.annotations = annotations;
        this.name = name;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesSeq(this.name, this.annotations).iterator();
    }

    /**
     * Add annotation.
     *
     * @param annotation Annotation
     * @return This object
     */
    public DirectivesAnnotations add(final DirectivesAnnotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof DirectivesAnnotations) {
            final DirectivesAnnotations directives = (DirectivesAnnotations) other;
            result = Objects.equals(this.annotations, directives.annotations)
                && Objects.equals(this.name, directives.name);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.annotations, this.name);
    }

    @Override
    public String toString() {
        return String.format(
            "DirectivesAnnotations(annotations=%s, name=%s)", this.annotations, this.name
        );
    }
}
