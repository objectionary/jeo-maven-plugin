/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesPlainAnnotationValue;
import org.objectweb.asm.AnnotationVisitor;
import org.xembly.Directive;

/**
 * An annotation value that is a plain value.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodePlainAnnotationValue implements BytecodeAnnotationValue {

    /**
     * The name of the annotation property.
     */
    private final String name;

    /**
     * The actual value.
     */
    private final Object value;

    /**
     * Constructor.
     * @param name The name of the annotation property.
     * @param value The actual value.
     */
    public BytecodePlainAnnotationValue(final String name, final Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void writeTo(final AnnotationVisitor visitor) {
        visitor.visit(this.name, this.value);
    }

    @Override
    public Iterable<Directive> directives() {
        return new DirectivesPlainAnnotationValue(this.name, this.value);
    }
}
