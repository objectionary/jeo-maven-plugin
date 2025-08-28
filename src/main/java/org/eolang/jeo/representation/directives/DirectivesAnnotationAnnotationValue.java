/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
 * An annotation value that is itself an annotation.
 * @since 0.6
 */
public final class DirectivesAnnotationAnnotationValue implements Iterable<Directive> {

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * The name of the annotation property.
     */
    private final String name;

    /**
     * The descriptor of the annotation.
     */
    private final String descriptor;

    /**
     * Inner values.
     */
    private final List<Iterable<Directive>> values;

    /**
     * Constructor.
     * @param format Format of the directives.
     * @param name The name of the annotation property.
     * @param descriptor The descriptor of the annotation.
     * @param values The inner annotation values.
     * @checkstyle ParameterNumber (5 lines)
     */
    public DirectivesAnnotationAnnotationValue(
        final Format format,
        final String name,
        final String descriptor,
        final List<Iterable<Directive>> values
    ) {
        this.format = format;
        this.name = name;
        this.descriptor = descriptor;
        this.values = values;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "annotation-property",
            new RandName("a").toString(),
            Stream.concat(
                Stream.of(
                    new DirectivesValue(this.format, "ANNOTATION"),
                    new DirectivesValue(this.format, this.name),
                    new DirectivesValue(this.format, this.descriptor)
                ),
                this.values.stream()
            ).map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }
}
