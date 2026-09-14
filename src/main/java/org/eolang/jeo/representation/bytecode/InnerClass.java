/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Objects;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesAttribute;
import org.eolang.jeo.representation.directives.DirectivesValue;
import org.eolang.jeo.representation.directives.Format;
import org.eolang.jeo.representation.directives.NumName;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Inner class attribute.
 *
 * @since 0.4
 */
public final class InnerClass implements BytecodeAttribute {

    /**
     * Internal name of the class.
     */
    private final String name;

    /**
     * The internal name of the class or interface class is a member of.
     */
    private final String outer;

    /**
     * The simple name of the class.
     */
    private final String inner;

    /**
     * Access flags of the inner class as originally declared in the enclosing class.
     */
    private final int access;

    /**
     * Constructor.
     *
     * @param name Internal name of the class
     * @param outer The internal name of the class or interface class is a member of
     * @param inner The simple name of the class
     * @param access Access flags of the inner class as originally declared in the
     *  enclosing class
     */
    public InnerClass(
        final String name,
        final String outer,
        final String inner,
        final int access
    ) {
        this.name = name;
        this.outer = outer;
        this.inner = inner;
        this.access = access;
    }

    @Override
    public void write(final ClassVisitor clazz) {
        clazz.visitInnerClass(this.name, this.outer, this.inner, this.access);
    }

    @Override
    public void write(final MethodVisitor method, final AsmLabels labels) {
        throw new UnsupportedOperationException(
            String.format(
                "Inner class '%s' cannot be written to method attributes",
                this
            )
        );
    }

    @Override
    public DirectivesAttribute directives(final int index, final Format format) {
        return new DirectivesAttribute(
            "inner-class",
            new NumName("a", index).toString(),
            new DirectivesValue(format, "name", this.name),
            new DirectivesValue(format, "outer", this.outer),
            new DirectivesValue(format, "inner", this.inner),
            new DirectivesValue(format, "access", this.access)
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof InnerClass) {
            final InnerClass clazz = (InnerClass) other;
            result = this.access == clazz.access
                && Objects.equals(this.name, clazz.name)
                && Objects.equals(this.outer, clazz.outer)
                && Objects.equals(this.inner, clazz.inner);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.outer, this.inner, this.access);
    }

    @Override
    public String toString() {
        return String.format(
            "InnerClass(name=%s, outer=%s, inner=%s, access=%d)",
            this.name, this.outer, this.inner, this.access
        );
    }
}
