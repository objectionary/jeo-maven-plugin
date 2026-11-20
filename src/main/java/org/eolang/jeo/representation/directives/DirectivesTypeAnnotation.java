/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.bytecode.AnnotationScope;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives of Type Annotation.
 *
 * @since 0.15.0
 */
public final class DirectivesTypeAnnotation implements Iterable<Directive> {

    /**
     * Index of the annotation among other annotations.
     */
    private final int index;

    /**
     * A reference to the annotated type.
     */
    private final int ref;

    /**
     * The path to the annotated type argument, wildcard bound, array element type,
     * or static outer type within the referenced type.
     */
    private final String path;

    /**
     * The class descriptor of the annotation class.
     */
    private final String desc;

    /**
     * Visibility of the annotation.
     */
    private final boolean visible;

    /**
     * Annotation properties.
     */
    private final List<Iterable<Directive>> properties;

    /**
     * Scopes of the annotated local variable.
     * It is empty when the annotation does not target a local variable,
     * or when its range is borrowed from the enclosing local variable entry.
     * A scope without a start label borrows it from the enclosing context.
     */
    private final List<AnnotationScope> scopes;

    /**
     * Annotation value.
     */
    private final Format format;

    /**
     * Constructor.
     *
     * @param format Format of the directives
     * @param index Index of the annotation among other annotations
     * @param ref A reference to the annotated type
     * @param path The path to the annotated type argument, wildcard bound, array element type,
     * @param desc The class descriptor of the annotation class
     * @param visible Visibility of the annotation
     * @param properties Properties
     */
    public DirectivesTypeAnnotation(
        final Format format,
        final int index,
        final int ref,
        final String path,
        final String desc,
        final boolean visible,
        final List<Iterable<Directive>> properties
    ) {
        this(format, index, ref, path, desc, visible, properties, Collections.emptyList());
    }

    /**
     * Constructor.
     *
     * @param format Format of the directives
     * @param index Index of the annotation among other annotations
     * @param ref A reference to the annotated type
     * @param path The path to the annotated type argument, wildcard bound, array element type,
     * @param desc The class descriptor of the annotation class
     * @param visible Visibility of the annotation
     * @param properties Properties
     * @param scopes Scopes of the annotated local variable
     */
    public DirectivesTypeAnnotation(
        final Format format,
        final int index,
        final int ref,
        final String path,
        final String desc,
        final boolean visible,
        final List<Iterable<Directive>> properties,
        final List<AnnotationScope> scopes
    ) {
        this.format = format;
        this.index = index;
        this.ref = ref;
        this.path = path;
        this.desc = desc;
        this.visible = visible;
        this.properties = properties;
        this.scopes = scopes;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesSeq(
            String.format("type-annotation-%d", this.index),
            Stream.concat(
                Stream.concat(
                    Stream.of(
                        new DirectivesValue(0, this.format, this.ref),
                        new DirectivesValue(1, this.format, this.path),
                        new DirectivesValue(2, this.format, this.desc),
                        new DirectivesValue(3, this.format, this.visible)
                    ),
                    this.properties.stream()
                ),
                this.scope()
            ).map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }

    // Directives of the annotated local variable scopes.
    // A scope without a start label borrows it from the enclosing context,
    // so only its end and slot are written. Explicit scopes carry all three.
    private Stream<Iterable<Directive>> scope() {
        final List<Iterable<Directive>> all = new ArrayList<>(0);
        for (final AnnotationScope scope : this.scopes) {
            if (scope.start() != null) {
                all.add(new DirectivesValue(this.format, "scope-start", scope.start().uid()));
            }
            all.add(new DirectivesValue(this.format, "scope-end", scope.end().uid()));
            all.add(new DirectivesValue(this.format, "scope-slot", scope.slot()));
        }
        return all.stream();
    }
}
