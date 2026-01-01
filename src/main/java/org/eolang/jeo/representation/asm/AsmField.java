/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import org.eolang.jeo.representation.bytecode.BytecodeField;
import org.objectweb.asm.tree.FieldNode;

/**
 * Asm field.
 * Asm parser for a field.
 * @since 0.6
 */
final class AsmField {

    /**
     * Field node.
     */
    private final FieldNode node;

    /**
     * Constructor.
     * @param node Field node.
     */
    AsmField(final FieldNode node) {
        this.node = node;
    }

    /**
     * Convert asm field to domain field.
     * @return Domain field.
     */
    BytecodeField bytecode() {
        return new BytecodeField(
            this.node.name,
            this.node.desc,
            this.node.signature,
            this.node.value,
            this.node.access,
            new AsmAnnotations(this.node).bytecode()
        );
    }
}
