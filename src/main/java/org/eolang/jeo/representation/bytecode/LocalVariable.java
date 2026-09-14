/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Objects;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesLocalVariables;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.LocalVariableNode;
import org.xembly.Directive;

/**
 * Local variable attribute.
 * Represents `LocalVariableTable` entry from bytecode attributes.
 *
 * @since 0.6
 */
public final class LocalVariable implements BytecodeAttribute {

    /**
     * Index of the local variable in the local variable array.
     */
    private final int index;

    /**
     * Name of the local variable.
     */
    private final String name;

    /**
     * Descriptor of the local variable.
     */
    private final String descriptor;

    /**
     * Signature of the local variable.
     */
    private final String signature;

    /**
     * Start label.
     */
    private final BytecodeLabel start;

    /**
     * End label.
     */
    private final BytecodeLabel end;

    /**
     * Constructor.
     *
     * @param variable Local variable node
     */
    public LocalVariable(final LocalVariableNode variable) {
        this(
            variable.index,
            variable.name,
            variable.desc,
            variable.signature,
            new BytecodeLabel(variable.start.getLabel().toString()),
            new BytecodeLabel(variable.end.getLabel().toString())
        );
    }

    /**
     * Constructor.
     *
     * @param index Index of the local variable in the local variable array
     * @param name Name of the local variable
     * @param descriptor Descriptor of the local variable
     * @param signature Signature of the local variable
     * @param start Start label
     * @param end End label
     */
    public LocalVariable(
        final int index,
        final String name,
        final String descriptor,
        final String signature,
        final BytecodeLabel start,
        final BytecodeLabel end
    ) {
        this.index = index;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.start = start;
        this.end = end;
    }

    @Override
    public void write(final ClassVisitor clazz) {
        throw new UnsupportedOperationException(
            String.format(
                "Local variable '%s' cannot be written to class attributes",
                this
            )
        );
    }

    @Override
    public void write(final MethodVisitor method, final AsmLabels labels) {
        final String sig;
        if (this.signature == null || this.signature.isEmpty()) {
            sig = null;
        } else {
            sig = this.signature;
        }
        method.visitLocalVariable(
            this.name,
            this.descriptor,
            sig,
            labels.label(this.start),
            labels.label(this.end),
            this.index
        );
    }

    @Override
    public Iterable<Directive> directives(final int oindex, final Format format) {
        return new DirectivesLocalVariables(
            oindex,
            format,
            this.index,
            this.name,
            this.descriptor,
            this.signature,
            this.start.directives(0, format),
            this.end.directives(1, format)
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof LocalVariable) {
            final LocalVariable variable = (LocalVariable) other;
            result = this.index == variable.index
                && Objects.equals(this.name, variable.name)
                && Objects.equals(this.descriptor, variable.descriptor)
                && Objects.equals(this.signature, variable.signature)
                && Objects.equals(this.start, variable.start)
                && Objects.equals(this.end, variable.end);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.index, this.name, this.descriptor, this.signature, this.start, this.end
        );
    }

    @Override
    public String toString() {
        return String.format(
            "LocalVariable(index=%d, name=%s, descriptor=%s, signature=%s, start=%s, end=%s)",
            this.index, this.name, this.descriptor, this.signature, this.start, this.end
        );
    }
}
