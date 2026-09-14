/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Objects;
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
public final class BytecodeDefaultValue {

    /**
     * Annotation property as a value.
     */
    private final BytecodeAnnotationValue property;

    /**
     * Constructor.
     *
     * @param property Annotation property as a value
     */
    public BytecodeDefaultValue(final BytecodeAnnotationValue property) {
        this.property = property;
    }

    /**
     * Write the default value to the given visitor.
     *
     * @param mvisitor Visitor
     */
    public void writeTo(final MethodVisitor mvisitor) {
        final AnnotationVisitor visitor = mvisitor.visitAnnotationDefault();
        this.property.writeTo(visitor);
        visitor.visitEnd();
    }

    /**
     * Convert to directives.
     *
     * @param format Format of the directives
     * @return Directives
     */
    public Iterable<Directive> directives(final Format format) {
        return new DirectivesDefaultValue(this.property.directives(0, format));
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeDefaultValue) {
            result = Objects.equals(this.property, ((BytecodeDefaultValue) other).property);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.property);
    }

    @Override
    public String toString() {
        return String.format("BytecodeDefaultValue(property=%s)", this.property);
    }
}
