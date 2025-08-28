/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesEnumAnnotationValue;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.AnnotationVisitor;
import org.xembly.Directive;

/**
 * An annotation value that is an enumeration.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodeEnumAnnotationValue implements BytecodeAnnotationValue {

    /**
     * The name of the annotation property.
     */
    private final String name;

    /**
     * The descriptor of the enumeration.
     */
    private final String descriptor;

    /**
     * The actual enumeration value.
     */
    private final String value;

    /**
     * Constructor.
     * @param name The name of the annotation property.
     * @param descriptor The descriptor of the enumeration.
     * @param value The actual enumeration value.
     */
    public BytecodeEnumAnnotationValue(
        final String name, final String descriptor, final String value
    ) {
        this.name = name;
        this.descriptor = descriptor;
        this.value = value;
    }

    @Override
    public void writeTo(final AnnotationVisitor visitor) {
        visitor.visitEnum(this.name, this.descriptor, this.value);
    }

    @Override
    public Iterable<Directive> directives(final Format format) {
        return new DirectivesEnumAnnotationValue(format, this.name, this.descriptor, this.value);
    }
}
