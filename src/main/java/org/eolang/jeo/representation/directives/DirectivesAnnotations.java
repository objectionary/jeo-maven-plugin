/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directive;

/**
 * Directives Annotation.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
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
     * @param annotations Annotations.
     * @param name Name.
     */
    public DirectivesAnnotations(final List<Iterable<Directive>> annotations, final String name) {
        this.annotations = annotations;
        this.name = name;
    }

    /**
     * Constructor.
     */
    public DirectivesAnnotations() {
        this(new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param annotations Annotations.
     */
    public DirectivesAnnotations(final List<Iterable<Directive>> annotations) {
        this(annotations, "annotations");
    }

    /**
     * Constructor.
     * @param name Name.
     */
    DirectivesAnnotations(final String name) {
        this(new ArrayList<>(0), name);
    }

    /**
     * Constructor.
     * @param name Name.
     * @param annotations Annotations.
     */
    DirectivesAnnotations(final String name, final DirectivesAnnotation... annotations) {
        this(Arrays.asList(annotations), name);
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesSeq(this.name, this.annotations).iterator();
    }

    /**
     * Add annotation.
     * @param annotation Annotation.
     * @return This object.
     */
    public DirectivesAnnotations add(final DirectivesAnnotation annotation) {
        this.annotations.add(annotation);
        return this;
    }
}
