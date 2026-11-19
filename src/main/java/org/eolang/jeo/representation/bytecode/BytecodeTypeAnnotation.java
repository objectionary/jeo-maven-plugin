/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
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
     * End label of the annotated local variable scope.
     * It is present only for annotations that target a local variable scope
     * without a local variable table entry, where the annotation is attached
     * to the scope start label instead.
     */
    private final BytecodeLabel end;

    /**
     * Slot index of the annotated local variable scope.
     * It is present only together with {@link #end}.
     */
    private final Integer slot;

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
        this(ref, Objects.toString(path, ""), desc, visible, values);
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
        this(ref, path, desc, visible, values, null, null);
    }

    /**
     * Constructor.
     *
     * @param ref A reference to the annotated type
     * @param path The path to the annotated type argument, wildcard bound, array element type,
     * @param desc The class descriptor of the annotation class
     * @param visible Visibility of the annotation
     * @param values Properties
     * @param end End label of the annotated local variable scope
     * @param slot Slot index of the annotated local variable scope
     */
    public BytecodeTypeAnnotation(
        final int ref,
        final TypePath path,
        final String desc,
        final boolean visible,
        final List<BytecodeAnnotationValue> values,
        final BytecodeLabel end,
        final Integer slot
    ) {
        this(ref, Objects.toString(path, ""), desc, visible, values, end, slot);
    }

    /**
     * Constructor.
     *
     * @param ref A reference to the annotated type
     * @param path The path to the annotated type argument, wildcard bound, array element type,
     * @param desc The class descriptor of the annotation class
     * @param visible Visibility of the annotation
     * @param values Properties
     * @param end End label of the annotated local variable scope
     * @param slot Slot index of the annotated local variable scope
     */
    public BytecodeTypeAnnotation(
        final int ref,
        final String path,
        final String desc,
        final boolean visible,
        final List<BytecodeAnnotationValue> values,
        final BytecodeLabel end,
        final Integer slot
    ) {
        this.ref = ref;
        this.path = path;
        this.desc = desc;
        this.visible = visible;
        this.values = new ArrayList<>(values);
        this.end = end;
        this.slot = slot;
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
        final AnnotationVisitor visited = visitor.visitLocalVariableAnnotation(
            this.ref,
            TypePath.fromString(this.path),
            new Label[]{labels.label(variable.startLabel())},
            new Label[]{labels.label(variable.endLabel())},
            new int[]{variable.slot()},
            this.desc,
            this.visible
        );
        if (visited == null) {
            return;
        }
        this.values.forEach(v -> v.writeTo(visited));
        visited.visitEnd();
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
            this.end,
            this.slot
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
                && Objects.equals(this.end, annotation.end)
                && Objects.equals(this.slot, annotation.slot);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.ref, this.path, this.desc, this.visible, this.values, this.end, this.slot
        );
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeTypeAnnotation(ref=%d, path=%s, desc=%s, visible=%b, values=%s, end=%s, slot=%s)",
            this.ref,
            this.path,
            this.desc,
            this.visible,
            this.values,
            this.end,
            this.slot
        );
    }

    /**
     * Write type annotation that targets a local variable scope without
     * a local variable table entry. The annotation is written next to the
     * scope start label, while the scope end label and the slot index are
     * stored inside the annotation itself.
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
        if (this.end != null && this.slot != null) {
            final AnnotationVisitor visited = visitor.visitLocalVariableAnnotation(
                this.ref,
                TypePath.fromString(this.path),
                new Label[]{start},
                new Label[]{labels.label(this.end)},
                new int[]{this.slot},
                this.desc,
                this.visible
            );
            if (visited != null) {
                this.values.forEach(v -> v.writeTo(visited));
                visited.visitEnd();
            }
        }
    }
}
