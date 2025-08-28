/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesDefaultValue;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode default value.
 *
 * @since 0.3
 */
@ToString
@EqualsAndHashCode
public final class BytecodeDefaultValue {

    /**
     * Annotation property as a value.
     */
    private final BytecodeAnnotationValue property;

    /**
     * Constructor.
     * @param property Annotation property as a value.
     */
    public BytecodeDefaultValue(final BytecodeAnnotationValue property) {
        this.property = property;
    }

    /**
     * Write the default value to the given visitor.
     * @param mvisitor Visitor.
     */
    public void writeTo(final MethodVisitor mvisitor) {
        final AnnotationVisitor visitor = mvisitor.visitAnnotationDefault();
        this.property.writeTo(visitor);
        visitor.visitEnd();
    }

    public Iterable<Directive> directives(final Format format) {
        return new DirectivesDefaultValue(this.property.directives(format));
    }
}
