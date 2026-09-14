/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Objects;
import org.eolang.jeo.representation.directives.DirectivesEnumAnnotationValue;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.AnnotationVisitor;
import org.xembly.Directive;

/**
 * An annotation value that is an enumeration.
 *
 * @since 0.6
 */
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
     *
     * @param name The name of the annotation property
     * @param descriptor The descriptor of the enumeration
     * @param value The actual enumeration value
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
    public Iterable<Directive> directives(final int index, final Format format) {
        return new DirectivesEnumAnnotationValue(
            index, format, this.name, this.descriptor, this.value
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeEnumAnnotationValue) {
            final BytecodeEnumAnnotationValue enm = (BytecodeEnumAnnotationValue) other;
            result = Objects.equals(this.name, enm.name)
                && Objects.equals(this.descriptor, enm.descriptor)
                && Objects.equals(this.value, enm.value);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.descriptor, this.value);
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeEnumAnnotationValue(name=%s, descriptor=%s, value=%s)",
            this.name, this.descriptor, this.value
        );
    }
}
