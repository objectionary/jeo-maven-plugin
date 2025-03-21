/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.objectweb.asm.AnnotationVisitor;
import org.xembly.Directive;

/**
 * Bytecode annotation value.
 * All the instances of this class know how to write themselves to the given
 * {@link AnnotationVisitor}. Since the annotation values are not always plain
 * values, this interface is used to abstract the writing process.
 *
 * @since 0.3
 */
public interface BytecodeAnnotationValue {

    /**
     * Write the value to the given visitor.
     * @param visitor Visitor.
     */
    void writeTo(AnnotationVisitor visitor);

    Iterable<Directive> directives();
}
