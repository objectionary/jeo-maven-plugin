/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * An annotation value that is an array.
 * @since 0.6
 */
public final class DirectivesArrayAnnotationValue implements Iterable<Directive> {

    /**
     * Index of the annotation among other annotations.
     */
    private final int index;

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * The name of the annotation property.
     */
    private final String name;

    /**
     * The actual values.
     */
    private final List<Iterable<Directive>> values;

    /**
     * Constructor.
     * @param index Index of the annotation value among other annotation values.
     * @param format Format of the directives.
     * @param name The name of the annotation property.
     * @param children The actual values.
     * @checkstyle ParameterNumber (5 lines)
     */
    public DirectivesArrayAnnotationValue(
        final int index,
        final Format format,
        final String name,
        final List<Iterable<Directive>> children
    ) {
        this.index = index;
        this.format = format;
        this.name = name;
        this.values = children;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "annotation-property",
            new NumName("a", this.index).toString(),
            Stream.concat(
                Stream.of(
                    new DirectivesValue(0, this.format, "ARRAY"),
                    new DirectivesValue(1, this.format, this.name)
                ),
                this.values.stream()
            ).map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }
}
