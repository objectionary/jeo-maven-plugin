/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Locale;
import java.util.Optional;

/**
 * Bytecode byte array.
 * @since 0.8
 */
public final class BytecodeBytes {

    /**
     * Data type.
     */
    private final DataType vtype;

    /**
     * Bytes.
     */
    private final byte[] vbytes;

    /**
     * Constructor.
     * @param type Value type.
     * @param bytes Value bytes.
     */
    public BytecodeBytes(final String type, final byte[] bytes) {
        this(DataType.findByBase(type), bytes);
    }

    /**
     * Constructor.
     * @param type Value type.
     * @param bytes Value bytes.
     */
    private BytecodeBytes(final DataType type, final byte[] bytes) {
        this.vtype = type;
        this.vbytes = Optional.ofNullable(bytes).map(byte[]::clone).orElse(null);
    }

    /**
     * Represent the value as an object.
     * @param codec Codec.
     * @return Object.
     */
    public Object object(final Codec codec) {
        return codec.decode(this.vbytes, this.vtype);
    }

    /**
     * Retrieve the type of the value.
     * @return Type.
     */
    public String type() {
        return this.vtype.caption().toLowerCase(Locale.ROOT);
    }

}
