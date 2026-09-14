/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.directives.DirectivesTypeAnnotation;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;

/**
 * Bytecode type annotation.
 *
 * @since 0.15.0
 */
public final class BytecodeTypeAnnotation {

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
     * Properties.
     */
    private final List<BytecodeAnnotationValue> values;

    /**
     * Constructor.
     *
     * @param ref A reference to the annotated type
     * @param path The path to the annotated type argument, wildcard bound, array element type,
     * @param desc The class descriptor of the annotation class
     * @param visible Visibility of the annotation
     * @param values Properties
     */
    public BytecodeTypeAnnotation(
        final int ref,
        final TypePath path,
        final String desc,
        final boolean visible,
        final List<BytecodeAnnotationValue> values
    ) {
        this(ref, path.toString(), desc, visible, values);
    }

    /**
     * Constructor.
     *
     * @param ref A reference to the annotated type
     * @param path The path to the annotated type argument, wildcard bound, array element type,
     * @param desc The class descriptor of the annotation class
     * @param visible Visibility of the annotation
     * @param values Properties
     */
    private BytecodeTypeAnnotation(
        final int ref,
        final String path,
        final String desc,
        final boolean visible,
        final List<BytecodeAnnotationValue> values
    ) {
        this.ref = ref;
        this.path = path;
        this.desc = desc;
        this.visible = visible;
        this.values = values;
    }

    /**
     * Write type annotation.
     *
     * @param visitor Visitor to write to
     */
    public void write(final RecordComponentVisitor visitor) {
        final AnnotationVisitor visited = visitor.visitTypeAnnotation(
            this.ref, TypePath.fromString(this.path), this.desc, this.visible
        );
        this.values.forEach(v -> v.writeTo(visited));
    }

    /**
     * Convert to directives.
     *
     * @param index Index of the annotation
     * @param format Directives format
     * @return Directives
     */
    public DirectivesTypeAnnotation directives(final int index, final Format format) {
        final AtomicInteger counter = new AtomicInteger(0);
        return new DirectivesTypeAnnotation(
            format, index,
            this.ref,
            this.path,
            this.desc,
            this.visible,
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
        } else if (other instanceof BytecodeTypeAnnotation) {
            final BytecodeTypeAnnotation annotation = (BytecodeTypeAnnotation) other;
            result = this.ref == annotation.ref
                && this.visible == annotation.visible
                && Objects.equals(this.path, annotation.path)
                && Objects.equals(this.desc, annotation.desc)
                && Objects.equals(this.values, annotation.values);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.ref, this.path, this.desc, this.visible, this.values);
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeTypeAnnotation(ref=%d, path=%s, desc=%s, visible=%b, values=%s)",
            this.ref, this.path, this.desc, this.visible, this.values
        );
    }
}
