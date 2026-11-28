/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Arrays;
import java.util.Objects;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.asm.AsmUnknownAttribute;
import org.eolang.jeo.representation.directives.DirectivesUnknownAttribute;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Unknown custom attribute.
 *
 * @since 0.15.0
 */
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
     * Whether this attribute belongs to a method's Code attribute.
     */
    private final boolean code;

    /**
     * Constructor.
     *
     * @param type Type of the attribute
     * @param data Data of the attribute
     */
    public BytecodeUnknownAttribute(final String type, final byte[] data) {
        this(type, data, false);
    }

    /**
     * Constructor.
     *
     * @param type Type of the attribute
     * @param data Data of the attribute
     * @param code Whether the attribute belongs to a Code attribute
     */
    public BytecodeUnknownAttribute(final String type, final byte[] data, final boolean code) {
        this.type = type;
        if (data == null) {
            this.data = null;
        } else {
            this.data = data.clone();
        }
        this.code = code;
    }

    @Override
    public void write(final ClassVisitor clazz) {
        clazz.visitAttribute(new AsmUnknownAttribute(this.type, this.bytes()));
    }

    /**
     * Write to a field.
     *
     * @param field Field visitor
     */
    public void write(final FieldVisitor field) {
        field.visitAttribute(new AsmUnknownAttribute(this.type, this.bytes()));
    }

    @Override
    public void write(final MethodVisitor method, final AsmLabels labels) {
        method.visitAttribute(new AsmUnknownAttribute(this.type, this.bytes(), this.code));
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        return new DirectivesUnknownAttribute(format, index, this.type, this.bytes(), this.code);
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeUnknownAttribute) {
            final BytecodeUnknownAttribute attribute = (BytecodeUnknownAttribute) other;
            result = Objects.equals(this.type, attribute.type)
                && this.code == attribute.code
                && Arrays.equals(this.bytes(), attribute.bytes());
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.code, Arrays.hashCode(this.bytes()));
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeUnknownAttribute(type=%s, data=%s, code=%b)",
            this.type, Arrays.toString(this.bytes()), this.code
        );
    }

    private byte[] bytes() {
        final byte[] result;
        if (this.data == null) {
            result = new byte[0];
        } else {
            result = this.data;
        }
        return result;
    }
}
