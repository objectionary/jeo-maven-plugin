/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Objects;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode parameter annotations.
 *
 * @since 0.15.0
 */
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
     *
     * @param index Parameter index
     * @param annotations Parameter annotations
     */
    public BytecodeParamAnnotations(final int index, final BytecodeAnnotations annotations) {
        this.index = index;
        this.annotations = annotations;
    }

    /**
     * Write all parameter annotations to bytecode.
     *
     * @param visitor Method to write in
     */
    public void write(final MethodVisitor visitor) {
        this.annotations.write(this.index, visitor);
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeParamAnnotations) {
            final BytecodeParamAnnotations params = (BytecodeParamAnnotations) other;
            result = this.index == params.index
                && Objects.equals(this.annotations, params.annotations);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.index, this.annotations);
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeParamAnnotations(index=%d, annotations=%s)", this.index, this.annotations
        );
    }

    /**
     * Convert to directives.
     *
     * @param format Directive format
     * @return Xmir Directives
     */
    Iterable<Directive> directives(final Format format) {
        return this.annotations.directives(
            format, String.format("param-annotations-%d", this.index)
        );
    }
}
