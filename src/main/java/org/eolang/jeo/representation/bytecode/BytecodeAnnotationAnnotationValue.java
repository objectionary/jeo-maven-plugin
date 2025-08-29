/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesAnnotationAnnotationValue;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.AnnotationVisitor;
import org.xembly.Directive;

/**
 * An annotation value that is itself an annotation.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodeAnnotationAnnotationValue implements BytecodeAnnotationValue {

    /**
     * The name of the annotation property.
     */
    private final String name;

    /**
     * The descriptor of the annotation.
     */
    private final String descriptor;

    /**
     * The actual annotation values.
     */
    private final List<BytecodeAnnotationValue> values;

    /**
     * Constructor.
     * @param name The name of the annotation property.
     * @param descriptor The descriptor of the annotation.
     * @param values The actual annotation values.
     */
    public BytecodeAnnotationAnnotationValue(
        final String name,
        final String descriptor,
        final List<BytecodeAnnotationValue> values
    ) {
        this.name = name;
        this.descriptor = descriptor;
        this.values = values;
    }

    @Override
    public void writeTo(final AnnotationVisitor visitor) {
        final AnnotationVisitor annotation = visitor.visitAnnotation(this.name, this.descriptor);
        this.values.forEach(value -> value.writeTo(annotation));
        annotation.visitEnd();
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        final AtomicInteger counter = new AtomicInteger(0);
        return new DirectivesAnnotationAnnotationValue(
            index,
            format,
            this.name,
            this.descriptor,
            this.values.stream()
                .map(v -> v.directives(counter.getAndIncrement(), format))
                .collect(Collectors.toList())
        );
    }
}
