/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.List;
import org.eolang.jeo.representation.DefaultVersion;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ParameterNode;

/**
 * ASM method visitor that collects the {@code MethodParameters} attribute.
 *
 * <p>ASM drops that attribute together with the rest of the debug information, so it has to be
 * read by a separate visitor that does not ask for the code at all.</p>
 *
 * @since 0.6
 */
final class AsmParameter extends MethodVisitor {

    /**
     * Where to put what we find.
     */
    private final List<ParameterNode> bag;

    /**
     * Constructor.
     *
     * @param bag Where to put what we find
     */
    AsmParameter(final List<ParameterNode> bag) {
        super(new DefaultVersion().api());
        this.bag = bag;
    }

    @Override
    public void visitParameter(final String name, final int access) {
        this.bag.add(new ParameterNode(name, access));
        super.visitParameter(name, access);
    }
}
