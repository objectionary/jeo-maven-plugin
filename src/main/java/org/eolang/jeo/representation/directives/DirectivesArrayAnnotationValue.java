/*
 * The MIT License (MIT)
 *
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
 * An annotation value that is an array.
 * @since 0.6
 */
public final class DirectivesArrayAnnotationValue implements Iterable<Directive> {

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
     * @param name The name of the annotation property.
     * @param children The actual values.
     */
    public DirectivesArrayAnnotationValue(
        final String name, final List<Iterable<Directive>> children
    ) {
        this.name = name;
        this.values = children;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "annotation-property",
            Stream.concat(
                Stream.of(
                    new DirectivesValue("ARRAY"),
                    new DirectivesValue(this.name)
                ),
                this.values.stream()
            ).map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }
}
