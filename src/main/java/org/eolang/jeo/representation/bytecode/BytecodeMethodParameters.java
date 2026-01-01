/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesMethodParams;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * Bytecode parameters.
 * @since 0.4
 */
@ToString
@EqualsAndHashCode
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
     * Default constructor.
     */
    public BytecodeMethodParameters() {
        this(new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param descriptor Method descriptor.
     */
    public BytecodeMethodParameters(final String descriptor) {
        this(BytecodeMethodParameters.fromDescriptor(descriptor));
    }

    /**
     * Constructor.
     * @param params Parameters.
     */
    public BytecodeMethodParameters(final BytecodeMethodParameter... params) {
        this(Arrays.asList(params));
    }

    /**
     * Constructor.
     * @param params Parameters.
     */
    public BytecodeMethodParameters(final List<BytecodeMethodParameter> params) {
        this(params, new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param params Parameters.
     * @param annotations Parameter annotations.
     */
    public BytecodeMethodParameters(
        final List<BytecodeMethodParameter> params,
        final List<BytecodeParamAnnotations> annotations
    ) {
        this.params = params;
        this.annotations = annotations;
    }

    /**
     * Add annotation.
     * @param visitor Method visitor.
     */
    public void write(final MethodVisitor visitor) {
        this.params.forEach(param -> param.write(visitor));
        this.annotations.forEach(ann -> ann.write(visitor));
    }

    /**
     * Convert to directives.
     * @param format Format of the directives.
     * @return Directives.
     */
    public DirectivesMethodParams directives(final Format format) {
        return new DirectivesMethodParams(
            this.params.stream()
                .map(p -> p.directives(format))
                .collect(Collectors.toList()),
            this.annotations.stream().map(
                a -> a.directives(format)
            ).collect(Collectors.toList())
        );
    }

    /**
     * Create from descriptor.
     * @param descriptor Method descriptor.
     * @return Parameters.
     */
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
