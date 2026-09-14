/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Objects;
import org.eolang.jeo.representation.directives.DirectivesPlainAnnotationValue;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.AnnotationVisitor;
import org.xembly.Directive;

/**
 * An annotation value that is a plain value.
 *
 * @since 0.6
 */
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
     *
     * @param name The name of the annotation property
     * @param value The actual value
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
    public Iterable<Directive> directives(final int index, final Format format) {
        return new DirectivesPlainAnnotationValue(index, format, this.name, this.value);
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodePlainAnnotationValue) {
            final BytecodePlainAnnotationValue plain = (BytecodePlainAnnotationValue) other;
            result = Objects.equals(this.name, plain.name)
                && Objects.equals(this.value, plain.value);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.value);
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodePlainAnnotationValue(name=%s, value=%s)", this.name, this.value
        );
    }
}
