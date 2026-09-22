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

    AsmUnknownAttributeMethodVisitor(final Set<String> types) {
        super(Opcodes.ASM9);
        this.types = types;
    }

    @Override
    public void visitAttribute(final Attribute attribute) {
        this.types.add(attribute.type);
    }
}
