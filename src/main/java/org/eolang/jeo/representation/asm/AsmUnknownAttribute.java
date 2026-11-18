/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import org.eolang.jeo.representation.bytecode.BytecodeUnknownAttribute;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;

/**
 * Asm unknown attribute.
 *
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
     * Whether this attribute belongs to a method's Code attribute.
     */
    private final boolean code;

    /**
     * Constructs a new empty attribute.
     *
     * @param type The type of the attribute
     */
    public AsmUnknownAttribute(final String type) {
        this(type, false);
    }

    /**
     * Constructs a new empty attribute.
     *
     * @param type The type of the attribute
     * @param code Whether the attribute belongs to a Code attribute
     */
    public AsmUnknownAttribute(final String type, final boolean code) {
        this(type, AsmUnknownAttribute.EMPTY, code);
    }

    /**
     * Constructs a new unknown attribute.
     *
     * @param type The type of the attribute
     * @param data The raw data of the attribute
     */
    public AsmUnknownAttribute(final String type, final byte[] data) {
        this(type, data, false);
    }

    /**
     * Constructs a new unknown attribute.
     *
     * @param type The type of the attribute
     * @param data The raw data of the attribute
     * @param code Whether the attribute belongs to a Code attribute
     */
    public AsmUnknownAttribute(final String type, final byte[] data, final boolean code) {
        super(type);
        if (data == null) {
            this.data = null;
        } else {
            this.data = data.clone();
        }
        this.code = code;
    }

    /**
     * Convert to bytecode unknown attribute.
     *
     * @return Bytecode unknown attribute
     */
    public BytecodeUnknownAttribute bytecode() {
        return new BytecodeUnknownAttribute(this.type, this.bytes(), this.code);
    }

    @Override
    public boolean isUnknown() {
        return true;
    }

    @Override
    public boolean isCodeAttribute() {
        return this.code;
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
        return new AsmUnknownAttribute(this.type, reader.readBytes(offset, length), this.code);
    }

    @Override
    public ByteVector write(
        final ClassWriter writer,
        final byte[] bytecode,
        final int length,
        final int stack,
        final int locals
    ) {
        final byte[] bytes = this.bytes();
        return new ByteVector().putByteArray(bytes, 0, bytes.length);
    }

    private byte[] bytes() {
        final byte[] result;
        if (this.data == null) {
            result = AsmUnknownAttribute.EMPTY;
        } else {
            result = this.data;
        }
        return result;
    }
}
