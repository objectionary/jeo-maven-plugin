/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.ClassVisitor;

/**
 * The record component of a record class.
 * @since 0.14.0
 * @todo #1274:30min Implement annotations for record components.
 *  Currently, the BytecodeRecordComponent class does not handle annotations.
 *  This needs to be implemented to fully support record components in Java bytecode.
 */
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
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
    }

    /**
     * Write to class visitor.
     * @param clazz Class visitor
     */
    public void write(final ClassVisitor clazz) {
        clazz.visitRecordComponent(this.name, this.descriptor, this.signature);
    }
}
