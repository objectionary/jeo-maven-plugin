/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesRecordComponent;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.xembly.Directive;

/**
 * The record component of a record class.
 * @since 0.14.0
 */
@ToString
@EqualsAndHashCode
public final class BytecodeRecordComponent {

    /**
     * Name.
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
     * Bytecode annotations.
     */
    private final BytecodeAnnotations annotations;

    /**
     * Type annotations.
     */
    private final BytecodeTypeAnnotations types;

    /**
     * Constructor.
     * @param name Name
     * @param descriptor Descriptor
     * @param signature Signature
     */
    public BytecodeRecordComponent(
        final String name,
        final String descriptor,
        final String signature
    ) {
        this(name, descriptor, signature, new BytecodeAnnotations(), new BytecodeTypeAnnotations());
    }

    /**
     * Constructor.
     * @param name Name
     * @param descriptor Descriptor
     * @param signature Signature
     * @param annotations Annotations
     * @param types Type annotations
     * @checkstyle ParameterNumber (10 lines)
     */
    public BytecodeRecordComponent(
        final String name,
        final String descriptor,
        final String signature,
        final BytecodeAnnotations annotations,
        final BytecodeTypeAnnotations types
    ) {
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.annotations = annotations;
        this.types = types;
    }

    /**
     * Write to class visitor.
     * @param clazz Class visitor
     */
    public void write(final ClassVisitor clazz) {
        final RecordComponentVisitor visitor = clazz.visitRecordComponent(
            this.name, this.descriptor, this.signature
        );
        this.annotations.write(visitor);
        this.types.write(visitor);
    }

    /**
     * Convert to directives.
     * @param index Index of the record component
     * @param format Format of the directives
     * @return Directives
     */
    public Iterable<Directive> directives(final int index, final Format format) {
        return new DirectivesRecordComponent(
            format,
            index,
            this.name,
            this.descriptor,
            this.signature,
            this.annotations.directives(format),
            this.types.directives(format)
        );
    }
}
