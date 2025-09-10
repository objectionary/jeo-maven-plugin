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
 * Unknown custom attribute.
 * @since 0.15.0
 */
public final class BytecodeCustomAttribute implements BytecodeAttribute {

    @Override
    public void write(final ClassVisitor clazz) {
        // Isn't implemented yet
    }

    @Override
    public void write(final MethodVisitor method, final AsmLabels labels) {
        // Isn't implemented yet
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        return new Directives();
    }
}
