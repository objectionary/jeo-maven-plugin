/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.asm.AsmUnknownAttribute;
import org.eolang.jeo.representation.directives.DirectivesUnknownAttribute;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Unknown custom attribute.
 * @since 0.15.0
 */
@ToString
@EqualsAndHashCode
public final class BytecodeUnknownAttribute implements BytecodeAttribute {

    /**
     * Type of the attribute.
     */
    private final String type;

    /**
     * Data of the attribute.
     */
    private final byte[] data;

    /**
     * Constructor.
     * @param type Type of the attribute.
     * @param data Data of the attribute.
     */
    public BytecodeUnknownAttribute(final String type, final byte[] data) {
        this.type = type;
        this.data = Optional.ofNullable(data).orElse(new byte[0]).clone();
    }

    @Override
    public void write(final ClassVisitor clazz) {
        clazz.visitAttribute(new AsmUnknownAttribute(this.type, this.data));
    }

    @Override
    public void write(final MethodVisitor method, final AsmLabels labels) {
        method.visitAttribute(new AsmUnknownAttribute(this.type, this.data));
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        return new DirectivesUnknownAttribute(format, index, this.type, this.data);
    }
}
