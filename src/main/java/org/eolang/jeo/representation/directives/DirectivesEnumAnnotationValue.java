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
     * @param name The name of the annotation property.
     * @param descriptor The descriptor of the enumeration.
     * @param value The actual enumeration value.
     */
    public DirectivesEnumAnnotationValue(
        final String name, final String descriptor, final String value
    ) {
        this.name = name;
        this.descriptor = descriptor;
        this.value = value;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "annotation-property",
            new DirectivesValue("ENUM"),
            new DirectivesValue(Optional.ofNullable(this.name).orElse("")),
            new DirectivesValue(this.descriptor),
            new DirectivesValue(this.value)
        ).iterator();
    }
}
