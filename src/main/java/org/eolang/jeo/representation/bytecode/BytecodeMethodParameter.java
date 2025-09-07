/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesMethodParam;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Bytecode method parameter.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
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
     * @param index Index of the parameter.
     * @param type Type of the parameter.
     */
    public BytecodeMethodParameter(
        final int index,
        final Type type
    ) {
        this(index, null, type);
    }

    /**
     * Constructor.
     * @param index Index of the parameter.
     * @param name Name of the parameter.
     * @param type Type of the parameter.
     * @checkstyle ParameterNumberCheck (5 lines)
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
     * @param index Index of the parameter.
     * @param name Name of the parameter.
     * @param access Method parameter access.
     * @param type Type of the parameter.
     * @checkstyle ParameterNumberCheck (5 lines)
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
     * @param visitor Method visitor.
     */
    public void write(final MethodVisitor visitor) {
        visitor.visitParameter(this.name, this.access);
    }

    /**
     * Convert to directives.
     * @param format Directives format.
     * @return Directives.
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
}
