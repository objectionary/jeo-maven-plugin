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

/**
 * Bytecode attribute.
 * @since 0.4
 */
public interface BytecodeAttribute {

    /**
     * Write to class.
     * @param clazz Bytecode where to write.
     */
    void write(ClassVisitor clazz);

    /**
     * Write to method.
     * @param method Bytecode where to write.
     * @param labels Method labels.
     */
    void write(MethodVisitor method, AsmLabels labels);

    /**
     * Converts to directives.
     * @param format Format of the directives.
     * @return Directives.
     */
    Iterable<Directive> directives(Format format);

}
