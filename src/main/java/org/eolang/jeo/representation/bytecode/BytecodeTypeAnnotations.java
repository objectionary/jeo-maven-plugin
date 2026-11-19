/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesTypeAnnotations;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.RecordComponentVisitor;

/**
 * Bytecode type annotations.
 *
 * @since 0.15.0
 */
public final class BytecodeTypeAnnotations {

    /**
     * All type annotations.
     */
    private final List<BytecodeTypeAnnotation> annotations;

    /**
     * Constructor.
     */
    public BytecodeTypeAnnotations() {
        this(Collections.emptyList());
    }

    /**
     * Constructor.
     *
     * @param annotations All type annotations
     */
    public BytecodeTypeAnnotations(final BytecodeTypeAnnotation... annotations) {
        this(Arrays.asList(annotations));
    }

    /**
     * Constructor.
     *
     * @param annotations All type annotations
     */
    public BytecodeTypeAnnotations(final List<BytecodeTypeAnnotation> annotations) {
        this.annotations = new ArrayList<>(annotations);
    }

    /**
     * Write to visitor.
     *
     * @param visitor Visitor to write to
     */
    public void write(final RecordComponentVisitor visitor) {
        this.present().forEach(annotation -> annotation.write(visitor));
    }

    /**
     * Whether there are no annotations.
     *
     * @return True if there are no annotations
     */
    public boolean isEmpty() {
        return this.present().isEmpty();
    }

    /**
     * Write annotations that target instructions.
     * The annotations must be written right after their target instructions.
     *
     * @param visitor Visitor to write to
     */
    public void writeInsn(final MethodVisitor visitor) {
        this.present().forEach(annotation -> annotation.writeInsn(visitor));
    }

    /**
     * Write annotations that target try-catch blocks.
     *
     * @param visitor Visitor to write to
     */
    public void writeTryCatch(final MethodVisitor visitor) {
        this.present().forEach(annotation -> annotation.writeTryCatch(visitor));
    }

    /**
     * Write annotations that target a local variable.
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
        this.present().forEach(
            annotation -> annotation.writeLocalVariable(visitor, variable, labels)
        );
    }

    /**
     * Write annotations that target local variable scopes without
     * local variable table entries. Each annotation carries its own
     * scope end label and slot index.
     *
     * @param visitor Visitor to write to
     * @param start Label that opens the annotated variable scopes
     * @param labels Labels of the method
     */
    public void writeLocalVariableScopes(
        final MethodVisitor visitor,
        final Label start,
        final AsmLabels labels
    ) {
        this.present().forEach(
            annotation -> annotation.writeLocalVariableScope(visitor, start, labels)
        );
    }

    /**
     * Convert to directives.
     *
     * @param format Format of the directives
     * @return Directives with the name "annotations"
     */
    public DirectivesTypeAnnotations directives(final Format format) {
        final AtomicInteger index = new AtomicInteger(0);
        return new DirectivesTypeAnnotations(
            this.present().stream()
                .map(ann -> ann.directives(index.getAndIncrement(), format))
                .collect(Collectors.toList())
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeTypeAnnotations) {
            result = Objects.equals(
                this.present(), ((BytecodeTypeAnnotations) other).present()
            );
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.present());
    }

    @Override
    public String toString() {
        return String.format("BytecodeTypeAnnotations(annotations=%s)", this.present());
    }

    private List<BytecodeTypeAnnotation> present() {
        final List<BytecodeTypeAnnotation> result;
        if (this.annotations == null) {
            result = Collections.emptyList();
        } else {
            result = this.annotations;
        }
        return result;
    }
}
