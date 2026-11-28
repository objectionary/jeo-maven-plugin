/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesTypeAnnotation;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
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
     * Scopes of the annotated local variable.
     * An annotation may target several ranges of a local variable at once,
     * ASM models them with parallel start/end/index lists. All ranges of one
     * annotation are kept together here.
     * The list is empty when the range is borrowed from the context:
     * the enclosing local variable entry or the label that opens the scope.
     */
    private final List<AnnotationScope> scopes;

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
        this(ref, Objects.toString(path, ""), desc, visible, values, Collections.emptyList());
    }

    /**
     * Constructor.
     *
     * @param ref A reference to the annotated type
     * @param path The path to the annotated type argument, wildcard bound, array element type,
     * @param desc The class descriptor of the annotation class
     * @param visible Visibility of the annotation
     * @param values Properties
     * @param scopes Scopes of the annotated local variable
     */
    public BytecodeTypeAnnotation(
        final int ref,
        final TypePath path,
        final String desc,
        final boolean visible,
        final List<BytecodeAnnotationValue> values,
        final List<AnnotationScope> scopes
    ) {
        this(ref, Objects.toString(path, ""), desc, visible, values, scopes);
    }

    /**
     * Constructor.
     *
     * @param ref A reference to the annotated type
     * @param path The path to the annotated type argument, wildcard bound, array element type,
     * @param desc The class descriptor of the annotation class
     * @param visible Visibility of the annotation
     * @param values Properties
     * @param scopes Scopes of the annotated local variable
     */
    public BytecodeTypeAnnotation(
        final int ref,
        final String path,
        final String desc,
        final boolean visible,
        final List<BytecodeAnnotationValue> values,
        final List<AnnotationScope> scopes
    ) {
        this.ref = ref;
        this.path = path;
        this.desc = desc;
        this.visible = visible;
        this.values = new ArrayList<>(values);
        this.scopes = new ArrayList<>(scopes);
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
        if (visited == null) {
            return;
        }
        this.values.forEach(v -> v.writeTo(visited));
        visited.visitEnd();
    }

    /**
     * Write type annotation that targets an instruction.
     * The bytecode offset of the target instruction is taken from the
     * most recently written instruction, so this method must be called
     * right after that instruction is written.
     *
     * @param visitor Visitor to write to
     */
    public void writeInsn(final MethodVisitor visitor) {
        final AnnotationVisitor visited = visitor.visitInsnAnnotation(
            this.ref, TypePath.fromString(this.path), this.desc, this.visible
        );
        if (visited == null) {
            return;
        }
        this.values.forEach(v -> v.writeTo(visited));
        visited.visitEnd();
    }

    /**
     * Write type annotation that targets a try-catch block.
     *
     * @param visitor Visitor to write to
     */
    public void writeTryCatch(final MethodVisitor visitor) {
        final AnnotationVisitor visited = visitor.visitTryCatchAnnotation(
            this.ref, TypePath.fromString(this.path), this.desc, this.visible
        );
        if (visited == null) {
            return;
        }
        this.values.forEach(v -> v.writeTo(visited));
        visited.visitEnd();
    }

    /**
     * Write type annotation that targets a local variable.
     * All scopes of the annotation are written together, so an annotation
     * with a split live range is restored as one annotation with several
     * ranges, exactly as it was in the original class file.
     *
     * @param visitor Visitor to write to
     * @param variable Annotated local variable
     * @param labels Labels of the method
     */
    public void writeLocalVariable(
        final MethodVisitor visitor,
        final LocalVariable variable,
        final AsmLabels labels
    ) {
        final List<AnnotationScope> ranges;
        if (this.scopes.isEmpty()) {
            ranges = Collections.singletonList(
                new AnnotationScope(
                    variable.startLabel(), variable.endLabel(), variable.slot()
                )
            );
        } else {
            ranges = this.scopes;
        }
        this.writeRanges(visitor, ranges, labels, null);
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
                .collect(Collectors.toList()),
            this.scopes
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
                && Objects.equals(this.values, annotation.values)
                && Objects.equals(this.scopes, annotation.scopes);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.ref, this.path, this.desc, this.visible, this.values, this.scopes
        );
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeTypeAnnotation(ref=%d, path=%s, desc=%s, visible=%b, values=%s, scopes=%s)",
            this.ref,
            this.path,
            this.desc,
            this.visible,
            this.values,
            this.scopes
        );
    }

    /**
     * Write type annotation that targets a local variable scope without
     * a local variable table entry. The annotation is written after the
     * whole method code is visited, when all the labels are already bound
     * to their bytecode offsets.
     *
     * @param visitor Visitor to write to
     * @param start Label that opens the annotated variable scope
     * @param labels Labels of the method
     */
    void writeLocalVariableScope(
        final MethodVisitor visitor,
        final Label start,
        final AsmLabels labels
    ) {
        if (!this.scopes.isEmpty()) {
            this.writeRanges(visitor, this.scopes, labels, start);
        }
    }

    // Write type annotation that targets a local variable with the given ranges.
    // Ranges without an explicit start label fall back to the given one.
    private void writeRanges(
        final MethodVisitor visitor,
        final List<AnnotationScope> ranges,
        final AsmLabels labels,
        final Label fallback
    ) {
        final Label[] starts = new Label[ranges.size()];
        final Label[] ends = new Label[ranges.size()];
        final int[] slots = new int[ranges.size()];
        for (int position = 0; position < ranges.size(); ++position) {
            final AnnotationScope scope = ranges.get(position);
            if (scope.start() == null) {
                starts[position] = fallback;
            } else {
                starts[position] = labels.label(scope.start());
            }
            ends[position] = labels.label(scope.end());
            slots[position] = scope.slot();
        }
        final AnnotationVisitor visited = visitor.visitLocalVariableAnnotation(
            this.ref,
            TypePath.fromString(this.path),
            starts,
            ends,
            slots,
            this.desc,
            this.visible
        );
        if (visited == null) {
            return;
        }
        this.values.forEach(v -> v.writeTo(visited));
        visited.visitEnd();
    }
}
