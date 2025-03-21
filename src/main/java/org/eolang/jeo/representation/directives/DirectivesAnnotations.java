/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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
     * Random number generator.
     */
    private static final Random RANDOM = new SecureRandom();

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
    DirectivesAnnotations() {
        this(new ArrayList<>(0));
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

    /**
     * Constructor.
     * @param annotations Annotations.
     */
    private DirectivesAnnotations(final List<Iterable<Directive>> annotations) {
        this(
            annotations,
            String.format(
                String.format(
                    "annotations-%d",
                    Math.abs(DirectivesAnnotations.RANDOM.nextInt())
                )
            )
        );
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesOptionalSeq(this.name, this.annotations).iterator();
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
