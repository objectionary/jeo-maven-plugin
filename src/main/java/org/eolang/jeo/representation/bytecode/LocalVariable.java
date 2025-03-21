/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesAttribute;
import org.eolang.jeo.representation.directives.DirectivesValue;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.LocalVariableNode;

/**
 * Local variable attribute.
 * Represents `LocalVariableTable` entry from bytecode attributes.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
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
     * @param variable Local variable node.
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
     * @param index Index of the local variable in the local variable array.
     * @param name Name of the local variable.
     * @param descriptor Descriptor of the local variable.
     * @param signature Signature of the local variable.
     * @param start Start label.
     * @param end End label.
     * @checkstyle ParameterNumberCheck (10 lines)
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
        method.visitLocalVariable(
            this.name,
            this.descriptor,
            this.signature,
            labels.label(this.start),
            labels.label(this.end),
            this.index
        );
    }

    @Override
    public DirectivesAttribute directives() {
        return new DirectivesAttribute(
            "local-variable",
            new DirectivesValue("", this.index),
            new DirectivesValue("", this.name),
            new DirectivesValue("", this.descriptor),
            new DirectivesValue("", this.signature),
            this.start.directives(),
            this.end.directives()
        );
    }
}
