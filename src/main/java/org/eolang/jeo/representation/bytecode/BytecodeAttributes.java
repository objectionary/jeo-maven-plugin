/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesAttributes;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Bytecode attributes.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodeAttributes {

    /**
     * All attributes.
     */
    private final List<BytecodeAttribute> all;

    /**
     * Constructor.
     * @param all All attributes.
     */
    public BytecodeAttributes(final BytecodeAttribute... all) {
        this(Arrays.asList(all));
    }

    /**
     * Constructor.
     * @param all All attributes.
     */
    public BytecodeAttributes(final List<BytecodeAttribute> all) {
        this.all = all;
    }

    /**
     * Convert to directives.
     * @param name Name of the attributes in EO representation.
     * @return Directives.
     */
    public DirectivesAttributes directives(final String name) {
        return new DirectivesAttributes(
            name,
            this.all.stream().map(BytecodeAttribute::directives).collect(Collectors.toList())
        );
    }

    /**
     * Write to class.
     * @param clazz Bytecode where to write.
     */
    void write(final ClassVisitor clazz) {
        this.all.forEach(attr -> attr.write(clazz));
    }

    /**
     * Write to method.
     * @param method Bytecode where to write.
     * @param labels Method labels.
     */
    void write(final MethodVisitor method, final AsmLabels labels) {
        this.all.forEach(attr -> attr.write(method, labels));
    }
}
