/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesAttributes;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Bytecode attributes.
 *
 * @since 0.6
 */
public final class BytecodeAttributes {

    /**
     * All attributes.
     */
    private final List<BytecodeAttribute> all;

    /**
     * Constructor.
     *
     * @param all All attributes
     */
    public BytecodeAttributes(final BytecodeAttribute... all) {
        this(Arrays.asList(all));
    }

    /**
     * Constructor.
     *
     * @param all All attributes
     */
    public BytecodeAttributes(final List<BytecodeAttribute> all) {
        this.all = all;
    }

    /**
     * Convert to directives.
     *
     * @param format Format of directives
     * @param name Name of the attributes in EO representation
     * @return Directives
     */
    public DirectivesAttributes directives(final Format format, final String name) {
        final AtomicInteger counter = new AtomicInteger(0);
        return new DirectivesAttributes(
            name,
            this.all.stream()
                .map(a -> a.directives(counter.getAndIncrement(), format))
                .collect(Collectors.toList())
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeAttributes) {
            result = Objects.equals(this.all, ((BytecodeAttributes) other).all);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.all);
    }

    @Override
    public String toString() {
        return String.format("BytecodeAttributes(all=%s)", this.all);
    }

    /**
     * Write to class.
     *
     * @param clazz Bytecode where to write
     */
    void write(final ClassVisitor clazz) {
        this.all.forEach(attr -> attr.write(clazz));
    }

    /**
     * Write to method.
     *
     * @param method Bytecode where to write
     * @param labels Method labels
     */
    void write(final MethodVisitor method, final AsmLabels labels) {
        this.all.forEach(attr -> attr.write(method, labels));
    }
}
