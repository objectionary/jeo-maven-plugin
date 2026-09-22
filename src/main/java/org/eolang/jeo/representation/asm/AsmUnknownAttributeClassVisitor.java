/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Set;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * ASM visitor that collects custom attribute names from a class.
 *
 * @since 0.15.0
 */
final class AsmUnknownAttributeClassVisitor extends ClassVisitor {

    /** Names collected so far. */
    private final Set<String> types;

    AsmUnknownAttributeClassVisitor(final Set<String> types) {
        super(Opcodes.ASM9);
        this.types = types;
    }

    @Override
    public void visitAttribute(final Attribute attribute) {
        this.types.add(attribute.type);
    }

    @Override
    public FieldVisitor visitField(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final Object value
    ) {
        return new AsmUnknownAttributeFieldVisitor(this.types);
    }

    @Override
    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String[] exceptions
    ) {
        return new AsmUnknownAttributeMethodVisitor(this.types);
    }
}
