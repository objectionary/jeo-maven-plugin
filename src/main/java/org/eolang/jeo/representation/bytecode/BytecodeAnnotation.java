/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesAnnotation;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode annotation.
 * @since 0.2
 */
@ToString
@EqualsAndHashCode
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
     * @param descriptor Descriptor.
     * @param visible Visible.
     */
    public BytecodeAnnotation(final String descriptor, final boolean visible) {
        this(descriptor, visible, new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param descriptor Descriptor.
     * @param visible Visible.
     * @param vals Properties.
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
     * @param visitor Visitor.
     * @return This.
     */
    public BytecodeAnnotation write(final ClassVisitor visitor) {
        final AnnotationVisitor avisitor = visitor.visitAnnotation(this.descr, this.visible);
        this.values.forEach(property -> property.writeTo(avisitor));
        return this;
    }

    /**
     * Write method annotation.
     * @param visitor Visitor.
     * @return This.
     */
    public BytecodeAnnotation write(final MethodVisitor visitor) {
        final AnnotationVisitor avisitor = visitor.visitAnnotation(this.descr, this.visible);
        this.values.forEach(property -> property.writeTo(avisitor));
        return this;
    }

    /**
     * Write parameter annotation.
     * @param index Index of a parameter.
     * @param visitor Method visitor.
     * @return This.
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
     * @param visitor Visitor.
     * @return This.
     */
    public BytecodeAnnotation write(final FieldVisitor visitor) {
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
    public Iterable<Directive> directives() {
        return new DirectivesAnnotation(
            this.descr,
            this.visible,
            this.values.stream()
                .map(BytecodeAnnotationValue::directives)
                .collect(Collectors.toList())
        );
    }
}
