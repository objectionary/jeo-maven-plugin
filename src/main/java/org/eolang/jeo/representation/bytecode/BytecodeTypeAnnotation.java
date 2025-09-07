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
import org.eolang.jeo.representation.directives.DirectivesTypeAnnotation;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;

/**
 * Bytecode type annotation.
 * @since 0.15.0
 */
@ToString
@EqualsAndHashCode
public final class BytecodeTypeAnnotation {

    /**
     *A reference to the annotated type.
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
     * @param ref A reference to the annotated type.
     * @param path The path to the annotated type argument, wildcard bound, array element type,
     * @param desc The class descriptor of the annotation class.
     * @param visible Visibility of the annotation.
     * @param values Properties.
     * @checkstyle ParameterNumber (10 lines)
     */
    public BytecodeTypeAnnotation(
        final int ref,
        final TypePath path,
        final String desc,
        final boolean visible,
        final List<BytecodeAnnotationValue> values
    ) {
        this.ref = ref;
        this.path = path.toString();
        this.desc = desc;
        this.visible = visible;
        this.values = values;
    }

    /**
     * Write type annotation.
     * @param visitor Visitor to write to.
     */
    public void write(final RecordComponentVisitor visitor) {
        final AnnotationVisitor visited = visitor.visitTypeAnnotation(
            this.ref, TypePath.fromString(this.path), this.desc, this.visible
        );
        this.values.forEach(v -> v.writeTo(visited));
    }

    /**
     * Convert to directives.
     * @param index Index of the annotation.
     * @param format Directives format.
     * @return Directives.
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
}
