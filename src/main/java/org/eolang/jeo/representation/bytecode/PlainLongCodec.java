/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.nio.ByteBuffer;

/**
 * Codec that saves long as a plain byte array.
 * The delegate codec encodes all the rest data types.
 * @since 0.8
 */
public final class PlainLongCodec implements Codec {

    /**
     * Delegate codec.
     */
    private final Codec origin;

    /**
     * Constructor.
     * @param delegate Origin codec.
     */
    public PlainLongCodec(final Codec delegate) {
        this.origin = delegate;
    }

    @Override
    public byte[] encode(final Object object, final DataType type) {
        final byte[] result;
        if (type == DataType.LONG) {
            result = ByteBuffer.allocate(Long.BYTES).putLong((long) object).array();
        } else {
            result = this.origin.encode(object, type);
        }
        return result;
    }

    @Override
    public Object decode(final byte[] bytes, final DataType type) {
        final Object result;
        if (type == DataType.LONG) {
            result = ByteBuffer.wrap(bytes).getLong();
        } else {
            result = this.origin.decode(bytes, type);
        }
        return result;
    }
}
