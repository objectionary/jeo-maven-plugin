/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Bytecode annotation.
 * @since 0.2
 * @todo #488:90min Refactor Annotations Implementation.
 *  Current implementation of annotations mapping is rather complicated.
 *  I would say it's over-engineered. We have a lot of classes and interfaces
 *  that are used to represent annotations in different formats. We should
 *  refactor this implementation to make it simpler and more readable.
 */
@ToString
@EqualsAndHashCode
public final class BytecodeAnnotation implements BytecodeAnnotationValue {

    /**
     * Descriptor.
     */
    private final String descriptor;

    /**
     * Visible.
     */
    private final boolean visible;

    /**
     * Properties.
     */
    private final List<BytecodeAnnotationProperty> properties;

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
     * @param properties Properties.
     */
    public BytecodeAnnotation(
        final String descriptor,
        final boolean visible,
        final List<BytecodeAnnotationProperty> properties
    ) {
        this.descriptor = descriptor;
        this.visible = visible;
        this.properties = properties;
    }

    /**
     * Write class annotation.
     * @param visitor Visitor.
     * @return This.
     */
    public BytecodeAnnotation write(final ClassVisitor visitor) {
        final AnnotationVisitor avisitor = visitor.visitAnnotation(this.descriptor, this.visible);
        this.properties.forEach(property -> property.writeTo(avisitor));
        return this;
    }

    /**
     * Write method annotation.
     * @param visitor Visitor.
     * @return This.
     */
    public BytecodeAnnotation write(final MethodVisitor visitor) {
        final AnnotationVisitor avisitor = visitor.visitAnnotation(this.descriptor, this.visible);
        this.properties.forEach(property -> property.writeTo(avisitor));
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
            index, this.descriptor, this.visible
        );
        this.properties.forEach(property -> property.writeTo(avisitor));
        return this;
    }

    /**
     * Write field annotation.
     * @param visitor Visitor.
     * @return This.
     */
    public BytecodeAnnotation write(final FieldVisitor visitor) {
        final AnnotationVisitor avisitor = visitor.visitAnnotation(this.descriptor, this.visible);
        this.properties.forEach(property -> property.writeTo(avisitor));
        return this;
    }

    @Override
    public void writeTo(final AnnotationVisitor visitor) {
        final AnnotationVisitor inner = visitor.visitAnnotation(this.descriptor, this.descriptor);
        this.properties.forEach(property -> property.writeTo(inner));
    }

    /**
     * Descriptor.
     * @return Descriptor.
     */
    public String descriptor() {
        return this.descriptor;
    }

    /**
     * @todo
     * Visible.
     * @return Visible.
     */
    public boolean visible() {
        return this.visible;
    }
}
