/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Objects;
import org.eolang.jeo.representation.directives.DirectivesField;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

/**
 * Bytecode field.
 *
 * @since 0.2
 */
public final class BytecodeField {

    /**
     * Field name.
     */
    private final String name;

    /**
     * Descriptor.
     */
    private final String descriptor;

    /**
     * Signature.
     */
    private final String signature;

    /**
     * Set value.
     */
    private final Object value;

    /**
     * Access.
     */
    private final int access;

    /**
     * Annotations.
     */
    private final BytecodeAnnotations annotations;

    /**
     * Constructor.
     *
     * @param name Name
     * @param descr Descriptor
     * @param signature Signature
     * @param value Value
     * @param access Access
     */
    public BytecodeField(
        final String name,
        final String descr,
        final String signature,
        final Object value,
        final int access
    ) {
        this(name, descr, signature, value, access, new BytecodeAnnotations());
    }

    /**
     * Constructor.
     *
     * @param name Name
     * @param descriptor Descriptor
     * @param signature Signature
     * @param value Value
     * @param access Access
     * @param annotations Annotations
     */
    public BytecodeField(
        final String name,
        final String descriptor,
        final String signature,
        final Object value,
        final int access,
        final BytecodeAnnotations annotations
    ) {
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.value = value;
        this.access = access;
        this.annotations = annotations;
    }

    /**
     * Write field to a class.
     *
     * @param visitor Visitor
     */
    public void write(final ClassVisitor visitor) {
        final FieldVisitor fvisitor = visitor.visitField(
            this.access,
            this.name,
            this.descriptor,
            this.signature,
            this.value
        );
        this.annotations.annotations()
            .forEach(annotation -> annotation.write(fvisitor));
    }

    /**
     * Convert to directives.
     *
     * @param format Format of the directives
     * @return Directives
     */
    public DirectivesField directives(final Format format) {
        return new DirectivesField(
            format,
            this.access,
            this.name,
            this.descriptor,
            this.signature,
            this.value,
            this.annotations.directives(format, String.format("annotations-%s", this.name))
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeField) {
            final BytecodeField field = (BytecodeField) other;
            result = this.access == field.access
                && Objects.equals(this.name, field.name)
                && Objects.equals(this.descriptor, field.descriptor)
                && Objects.equals(this.signature, field.signature)
                && Objects.equals(this.value, field.value)
                && Objects.equals(this.annotations, field.annotations);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.name, this.descriptor, this.signature, this.value, this.access, this.annotations
        );
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeField(name=%s, descriptor=%s, signature=%s, value=%s, access=%d, annotations=%s)",
            this.name, this.descriptor, this.signature, this.value, this.access, this.annotations
        );
    }
}
