/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.directives.DirectivesAnnotationAnnotationValue;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.AnnotationVisitor;
import org.xembly.Directive;

/**
 * An annotation value that is itself an annotation.
 *
 * @since 0.6
 */
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
     *
     * @param name The name of the annotation property
     * @param descriptor The descriptor of the annotation
     * @param values The actual annotation values
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

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeAnnotationAnnotationValue) {
            final BytecodeAnnotationAnnotationValue value =
                (BytecodeAnnotationAnnotationValue) other;
            result = Objects.equals(this.name, value.name)
                && Objects.equals(this.descriptor, value.descriptor)
                && Objects.equals(this.values, value.values);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.descriptor, this.values);
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeAnnotationAnnotationValue(name=%s, descriptor=%s, values=%s)",
            this.name, this.descriptor, this.values
        );
    }
}
