/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Optional;
import org.xembly.Directive;

/**
 * An annotation value that is an enumeration.
 * @since 0.6
 */
public final class DirectivesEnumAnnotationValue implements Iterable<Directive> {

    /**
     * Index of the annotation value among other annotation values.
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
     * The descriptor of the enumeration.
     */
    private final String descriptor;

    /**
     * The actual enumeration value.
     */
    private final String value;

    /**
     * Constructor.
     * @param index Index of the annotation value among other annotation values.
     * @param format Format of the directives.
     * @param name The name of the annotation property.
     * @param descriptor The descriptor of the enumeration.
     * @param value The actual enumeration value.
     * @checkstyle ParameterNumber (5 lines)
     */
    public DirectivesEnumAnnotationValue(
        final int index,
        final Format format,
        final String name,
        final String descriptor,
        final String value
    ) {
        this.index = index;
        this.format = format;
        this.name = name;
        this.descriptor = descriptor;
        this.value = value;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "annotation-property",
            new NumName("e", this.index).toString(),
            new DirectivesValue(0, this.format, "ENUM"),
            new DirectivesValue(1, this.format, Optional.ofNullable(this.name).orElse("")),
            new DirectivesValue(2, this.format, this.descriptor),
            new DirectivesValue(3, this.format, this.value)
        ).iterator();
    }
}
