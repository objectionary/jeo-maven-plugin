/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Objects;
import org.eolang.jeo.representation.directives.DirectivesMethodParam;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Bytecode method parameter.
 *
 * @since 0.6
 */
public final class BytecodeMethodParameter {

    /**
     * Index of the parameter.
     */
    private final int index;

    /**
     * Name of the parameter.
     */
    private final String name;

    /**
     * Method parameter access.
     */
    private final int access;

    /**
     * Type of the parameter.
     */
    private final Type type;

    /**
     * Constructor.
     *
     * @param index Index of the parameter
     * @param type Type of the parameter
     */
    public BytecodeMethodParameter(final int index, final Type type) {
        this(index, null, type);
    }

    /**
     * Constructor.
     *
     * @param index Index of the parameter
     * @param name Name of the parameter
     * @param type Type of the parameter
     */
    public BytecodeMethodParameter(
        final int index,
        final String name,
        final Type type
    ) {
        this(index, name, 0, type);
    }

    /**
     * Constructor.
     *
     * @param index Index of the parameter
     * @param name Name of the parameter
     * @param access Method parameter access
     * @param type Type of the parameter
     */
    public BytecodeMethodParameter(
        final int index,
        final String name,
        final int access,
        final Type type
    ) {
        this.index = index;
        this.name = name;
        this.access = access;
        this.type = type;
    }

    /**
     * Write to the method visitor.
     *
     * @param visitor Method visitor
     */
    public void write(final MethodVisitor visitor) {
        visitor.visitParameter(this.name, this.access);
    }

    /**
     * Convert to directives.
     *
     * @param format Directives format
     * @return Directives
     */
    public Iterable<Directive> directives(final Format format) {
        return new DirectivesMethodParam(
            format,
            this.index,
            this.name,
            this.access,
            this.type
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeMethodParameter) {
            final BytecodeMethodParameter parameter = (BytecodeMethodParameter) other;
            result = this.index == parameter.index
                && this.access == parameter.access
                && Objects.equals(this.name, parameter.name)
                && Objects.equals(this.type, parameter.type);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.index, this.name, this.access, this.type);
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeMethodParameter(index=%d, name=%s, access=%d, type=%s)",
            this.index, this.name, this.access, this.type
        );
    }
}
