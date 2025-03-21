/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Locale;

/**
 * Bytecode value.
 * Represents a typed value in bytecode format.
 * @since 0.6
 */
public final class BytecodeObject {

    /**
     * Data type.
     */
    private final DataType vtype;

    /**
     * Bytes.
     */
    private final Object object;

    /**
     * Constructor.
     * @param value Value.
     */
    public BytecodeObject(final Object value) {
        this(DataType.findByData(value), value);
    }

    /**
     * Constructor.
     * @param type Value type.
     * @param bytes Value bytes.
     */
    private BytecodeObject(final DataType type, final Object bytes) {
        this.vtype = type;
        this.object = bytes;
    }

    /**
     * Retrieve the type of the value.
     * @return Type.
     */
    public String type() {
        return this.vtype.caption().toLowerCase(Locale.ROOT);
    }

    /**
     * Retrieve the value.
     * @return Value.
     */
    public Object value() {
        return this.object;
    }

    /**
     * Retrieve the bytes of the value.
     * @param codec Codec.
     * @return Bytes.
     */
    public byte[] encode(final Codec codec) {
        return codec.encode(this.object, this.vtype);
    }
}
