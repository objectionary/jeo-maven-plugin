/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.directives.DirectivesMethodParams;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * Bytecode parameters.
 *
 * @since 0.4
 */
public final class BytecodeMethodParameters {

    /**
     * Method parameter names.
     */
    private final List<BytecodeMethodParameter> params;

    /**
     * Method parameter annotations.
     */
    private final List<BytecodeParamAnnotations> annotations;

    /**
     * Number of parameters the visible annotations table has.
     */
    private final int visible;

    /**
     * Number of parameters the invisible annotations table has.
     */
    private final int invisible;

    /**
     * Default constructor.
     */
    public BytecodeMethodParameters() {
        this(new ArrayList<>(0));
    }

    /**
     * Constructor.
     *
     * @param descriptor Method descriptor
     */
    public BytecodeMethodParameters(final String descriptor) {
        this(BytecodeMethodParameters.fromDescriptor(descriptor));
    }

    /**
     * Constructor.
     *
     * @param params Parameters
     */
    public BytecodeMethodParameters(final BytecodeMethodParameter... params) {
        this(Arrays.asList(params));
    }

    /**
     * Constructor.
     *
     * @param params Parameters
     */
    public BytecodeMethodParameters(final List<BytecodeMethodParameter> params) {
        this(params, new ArrayList<>(0));
    }

    /**
     * Constructor.
     *
     * @param params Parameters
     * @param annotations Parameter annotations
     */
    public BytecodeMethodParameters(
        final List<BytecodeMethodParameter> params,
        final List<BytecodeParamAnnotations> annotations
    ) {
        this(params, annotations, 0, 0);
    }

    /**
     * Constructor.
     * @param params Parameters.
     * @param annotations Parameter annotations.
     * @param visible Number of parameters the visible annotations table has.
     * @param invisible Number of parameters the invisible annotations table has.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public BytecodeMethodParameters(
        final List<BytecodeMethodParameter> params,
        final List<BytecodeParamAnnotations> annotations,
        final int visible,
        final int invisible
    ) {
        this.params = params;
        this.annotations = annotations;
        this.visible = visible;
        this.invisible = invisible;
    }

    /**
     * Add annotation.
     *
     * @param visitor Method visitor
     */
    public void write(final MethodVisitor visitor) {
        this.params.forEach(param -> param.write(visitor));
        if (this.visible > 0) {
            visitor.visitAnnotableParameterCount(this.visible, true);
        }
        if (this.invisible > 0) {
            visitor.visitAnnotableParameterCount(this.invisible, false);
        }
        this.annotations.forEach(ann -> ann.write(visitor));
    }

    /**
     * Convert to directives.
     *
     * @param format Format of the directives
     * @return Directives
     */
    public DirectivesMethodParams directives(final Format format) {
        return new DirectivesMethodParams(
            this.params.stream()
                .map(p -> p.directives(format))
                .collect(Collectors.toList()),
            this.annotations.stream().map(
                a -> a.directives(format)
            ).collect(Collectors.toList()),
            format,
            this.visible,
            this.invisible
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeMethodParameters) {
            final BytecodeMethodParameters parameters = (BytecodeMethodParameters) other;
            result = Objects.equals(this.params, parameters.params)
                && Objects.equals(this.annotations, parameters.annotations);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.params, this.annotations);
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeMethodParameters(params=%s, annotations=%s)",
            this.params, this.annotations
        );
    }

    private static List<BytecodeMethodParameter> fromDescriptor(final String descriptor) {
        final Type[] types = Type.getArgumentTypes(descriptor);
        final int size = types.length;
        final List<BytecodeMethodParameter> params = new ArrayList<>(size);
        for (int index = 0; index < size; ++index) {
            params.add(new BytecodeMethodParameter(index, types[index]));
        }
        return params;
    }
}
