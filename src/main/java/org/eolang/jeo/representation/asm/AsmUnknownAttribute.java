/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Optional;
import org.eolang.jeo.representation.bytecode.BytecodeUnknownAttribute;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;

/**
 * Asm unknown attribute.
 * @since 0.15.0
 */
public final class AsmUnknownAttribute extends Attribute {

    /**
     * Empty byte array.
     */
    private static final byte[] EMPTY = new byte[0];

    /**
     * Attribute data.
     */
    private final byte[] data;

    /**
     * Constructs a new empty attribute.
     * @param type The type of the attribute.
     */
    public AsmUnknownAttribute(final String type) {
        this(type, AsmUnknownAttribute.EMPTY);
    }

    /**
     * Constructs a new unknown attribute.
     * @param type The type of the attribute.
     * @param data The raw data of the attribute.
     */
    public AsmUnknownAttribute(final String type, final byte[] data) {
        super(type);
        this.data = Optional.ofNullable(data).orElse(AsmUnknownAttribute.EMPTY).clone();
    }

    /**
     * Convert to bytecode unknown attribute.
     * @return Bytecode unknown attribute.
     */
    public BytecodeUnknownAttribute bytecode() {
        return new BytecodeUnknownAttribute(this.type, this.data);
    }

    @Override
    public boolean isUnknown() {
        return true;
    }

    @Override
    public Attribute read(
        final ClassReader reader,
        final int offset,
        final int length,
        final char[] buffer,
        final int caoffset,
        final Label[] labels
    ) {
        return new AsmUnknownAttribute(this.type, reader.readBytes(offset, length));
    }

    @Override
    public ByteVector write(
        final ClassWriter writer,
        final byte[] code,
        final int length,
        final int stack,
        final int locals
    ) {
        if (this.data == null) {
            throw new IllegalArgumentException(
                String.format("Attribute data is null for type `%s`", this.type)
            );
        }
        return new ByteVector().putByteArray(this.data, 0, this.data.length);
    }
}
