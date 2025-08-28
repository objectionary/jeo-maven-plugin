/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesArrayAnnotationValue;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.AnnotationVisitor;
import org.xembly.Directive;

/**
 * An annotation value that is an array.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodeArrayAnnotationValue implements BytecodeAnnotationValue {

    /**
     * The name of the annotation property.
     */
    private final String name;

    /**
     * The actual values.
     */
    private final List<BytecodeAnnotationValue> values;

    /**
     * Constructor.
     * @param name The name of the annotation property.
     * @param values The actual values.
     */
    public BytecodeArrayAnnotationValue(
        final String name, final List<BytecodeAnnotationValue> values
    ) {
        this.name = name;
        this.values = values;
    }

    @Override
    public void writeTo(final AnnotationVisitor visitor) {
        final AnnotationVisitor array = visitor.visitArray(this.name);
        this.values.forEach(value -> value.writeTo(array));
        array.visitEnd();
    }

    @Override
    public Iterable<Directive> directives(final Format format) {
        return new DirectivesArrayAnnotationValue(
            format,
            this.name,
            this.values.stream()
                .map(v -> v.directives(format))
                .collect(Collectors.toList())
        );
    }
}
