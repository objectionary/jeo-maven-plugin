/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode parameter annotations.
 * @since 0.15.0
 */
@ToString
@EqualsAndHashCode
public final class BytecodeParamAnnotations {

    /**
     * Parameter index.
     */
    private final int index;

    /**
     * Parameter annotations.
     */
    private final BytecodeAnnotations annotations;

    /**
     * Constructor.
     * @param index Parameter index.
     * @param annotations Parameter annotations.
     */
    public BytecodeParamAnnotations(final int index, final BytecodeAnnotations annotations) {
        this.index = index;
        this.annotations = annotations;
    }

    /**
     * Write all parameter annotations to bytecode.
     * @param visitor Method to write in.
     */
    public void write(final MethodVisitor visitor) {
        this.annotations.write(this.index, visitor);
    }

    /**
     * Convert to directives.
     * @param format Directive format.
     * @return Xmir Directives.
     */
    Iterable<Directive> directives(final Format format) {
        return this.annotations.directives(
            format, String.format("param-annotations-%d", this.index)
        );
    }
}
