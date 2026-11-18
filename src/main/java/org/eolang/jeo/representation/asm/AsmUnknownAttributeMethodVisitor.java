/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Set;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * ASM visitor that collects custom attribute names from a method.
 *
 * @since 0.15.0
 */
final class AsmUnknownAttributeMethodVisitor extends MethodVisitor {

    /** Names collected so far. */
    private final Set<String> types;

    /** Code-level attribute names collected so far. */
    private final Set<String> code;

    /** Whether Code attribute traversal has begun. */
    private boolean inside;

    AsmUnknownAttributeMethodVisitor(final Set<String> types, final Set<String> code) {
        super(Opcodes.ASM9);
        this.types = types;
        this.code = code;
    }

    @Override
    public void visitCode() {
        this.inside = true;
    }

    @Override
    public void visitAttribute(final Attribute attribute) {
        this.types.add(attribute.type);
        if (this.inside) {
            this.code.add(attribute.type);
        }
    }
}
