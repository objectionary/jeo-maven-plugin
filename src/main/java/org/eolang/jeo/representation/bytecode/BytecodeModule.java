/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Bytecode module.
 * @since 0.14.0
 * @todo #1274:30min Implement disassembling module attribute.
 *  The module attribute is represented by ModuleNode in ASM.
 *  We need to implement the BytecodeModule class to handle this attribute.
 */
public final class BytecodeModule implements BytecodeAttribute {

    @Override
    public void write(final ClassVisitor ignore) {
        // Module attribute is not implemented yet.
    }

    @Override
    public void write(final MethodVisitor method, final AsmLabels labels) {
        throw new UnsupportedOperationException("Module attribute is not applicable to methods");
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        return new Directives();
    }
}
