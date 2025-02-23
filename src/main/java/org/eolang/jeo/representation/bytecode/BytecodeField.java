/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesField;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

/**
 * Bytecode field.
 * @since 0.2
 */
@ToString
@EqualsAndHashCode
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
     * @param name Name.
     * @param descr Descriptor.
     * @param signature Signature.
     * @param value Value.
     * @param access Access.
     * @checkstyle ParameterNumberCheck (5 lines)
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
     * @param name Name.
     * @param descriptor Descriptor.
     * @param signature Signature.
     * @param value Value.
     * @param access Access.
     * @param annotations Annotations.
     * @checkstyle ParameterNumberCheck (5 lines)
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
     * @param visitor Visitor.
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

    public DirectivesField directives() {
        return new DirectivesField(
            this.access,
            this.name,
            this.descriptor,
            this.signature,
            this.value,
            this.annotations.directives(String.format("annotations-%s", this.name))
        );
    }
}
