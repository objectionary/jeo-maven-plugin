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
import org.eolang.jeo.representation.directives.DirectivesAnnotation;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.xembly.Directive;

/**
 * Bytecode annotation.
 *
 * @since 0.2
 */
public final class BytecodeAnnotation implements BytecodeAnnotationValue {

    /**
     * Descriptor.
     */
    private final String descr;

    /**
     * Visible.
     */
    private final boolean visible;

    /**
     * Properties.
     */
    private final List<BytecodeAnnotationValue> values;

    /**
     * Constructor.
     *
     * @param descriptor Descriptor
     * @param visible Visible
     */
    public BytecodeAnnotation(final String descriptor, final boolean visible) {
        this(descriptor, visible, new ArrayList<>(0));
    }

    /**
     * Constructor.
     *
     * @param descriptor Descriptor
     * @param visible Visible
     * @param vals Properties
     */
    public BytecodeAnnotation(
        final String descriptor,
        final boolean visible,
        final List<BytecodeAnnotationValue> vals
    ) {
        this.descr = descriptor;
        this.visible = visible;
        this.values = vals;
    }

    /**
     * Write class annotation.
     *
     * @param visitor Visitor
     * @return This
     */
    public BytecodeAnnotation write(final ClassVisitor visitor) {
        final AnnotationVisitor avisitor = visitor.visitAnnotation(this.descr, this.visible);
        this.values.forEach(property -> property.writeTo(avisitor));
        return this;
    }

    /**
     * Write method annotation.
     *
     * @param visitor Visitor
     * @return This
     */
    public BytecodeAnnotation write(final MethodVisitor visitor) {
        final AnnotationVisitor avisitor = visitor.visitAnnotation(this.descr, this.visible);
        this.values.forEach(property -> property.writeTo(avisitor));
        return this;
    }

    /**
     * Write parameter annotation.
     *
     * @param index Index of a parameter
     * @param visitor Method visitor
     * @return This
     */
    public BytecodeAnnotation write(final int index, final MethodVisitor visitor) {
        final AnnotationVisitor avisitor = visitor.visitParameterAnnotation(
            index, this.descr, this.visible
        );
        this.values.forEach(property -> property.writeTo(avisitor));
        return this;
    }

    /**
     * Write field annotation.
     *
     * @param visitor Visitor
     * @return This
     */
    public BytecodeAnnotation write(final FieldVisitor visitor) {
        final AnnotationVisitor avisitor = visitor.visitAnnotation(this.descr, this.visible);
        this.values.forEach(property -> property.writeTo(avisitor));
        return this;
    }

    /**
     * Write record component annotation.
     *
     * @param visitor Visitor
     * @return This
     */
    public BytecodeAnnotation write(final RecordComponentVisitor visitor) {
        final AnnotationVisitor avisitor = visitor.visitAnnotation(this.descr, this.visible);
        this.values.forEach(property -> property.writeTo(avisitor));
        return this;
    }

    @Override
    public void writeTo(final AnnotationVisitor visitor) {
        final AnnotationVisitor inner = visitor.visitAnnotation(this.descr, this.descr);
        this.values.forEach(property -> property.writeTo(inner));
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        final AtomicInteger idx = new AtomicInteger(0);
        return new DirectivesAnnotation(
            index,
            format,
            this.descr,
            this.visible,
            this.values.stream()
                .map(v -> v.directives(idx.getAndIncrement(), format))
                .collect(Collectors.toList())
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeAnnotation) {
            final BytecodeAnnotation annotation = (BytecodeAnnotation) other;
            result = this.visible == annotation.visible
                && Objects.equals(this.descr, annotation.descr)
                && Objects.equals(this.values, annotation.values);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.descr, this.visible, this.values);
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeAnnotation(descr=%s, visible=%b, values=%s)",
            this.descr, this.visible, this.values
        );
    }
}
