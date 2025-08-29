/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.NamedDescriptor;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives Annotation.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
public final class DirectivesAnnotation implements Iterable<Directive> {

    /**
     * Index of the annotation among other annotations.
     */
    private final int index;

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * Annotation descriptor.
     */
    private final String descriptor;

    /**
     * Annotation visible.
     */
    private final boolean visible;

    /**
     * Annotation properties.
     */
    private final List<Iterable<Directive>> properties;

    /**
     * Constructor.
     * @param descriptor Descriptor.
     * @param visible Visible.
     */
    public DirectivesAnnotation(
        final String descriptor,
        final boolean visible
    ) {
        this(0, new Format(), descriptor, visible, new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param index Index.
     * @param format Format.
     * @param descriptor Descriptor.
     * @param visible Visible.
     * @param props Properties.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    @SafeVarargs
    public DirectivesAnnotation(
        final int index,
        final Format format,
        final String descriptor,
        final boolean visible,
        final Iterable<Directive>... props
    ) {
        this(index, format, descriptor, visible, Arrays.asList(props));
    }

    /**
     * Constructor.
     * @param index Index.
     * @param format Format.
     * @param descriptor Descriptor.
     * @param visible Visible.
     * @param properties Properties.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesAnnotation(
        final int index,
        final Format format,
        final String descriptor,
        final boolean visible,
        final List<Iterable<Directive>> properties
    ) {
        this.index = index;
        this.format = format;
        this.descriptor = descriptor;
        this.visible = visible;
        this.properties = properties;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "annotation",
            new NamedDescriptor(
                String.format("annotation-%d", this.index),
                this.descriptor
            ).encoded(),
            Stream.concat(
                Stream.of(
                    new DirectivesValue(0, this.format, this.descriptor),
                    new DirectivesValue(1, this.format, this.visible)
                ),
                this.properties.stream()
            ).map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }
}
