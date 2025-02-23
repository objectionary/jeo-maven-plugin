/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.nio.ByteBuffer;

/**
 * EO codec.
 * Converts primitive types to byte arrays and vice versa.
 * @since 0.8
 * @checkstyle CyclomaticComplexityCheck (500 lines)
 */
public final class EoCodec implements Codec {

    /**
     * Origin codec.
     */
    private final Codec origin;

    /**
     * Constructor.
     */
    public EoCodec() {
        this(new JavaCodec());
    }

    /**
     * Constructor.
     * @param delegate Origin codec.
     */
    private EoCodec(final Codec delegate) {
        this.origin = delegate;
    }

    @Override
    public byte[] encode(final Object object, final DataType type) {
        final byte[] result;
        switch (type) {
            case BOOL:
            case CHAR:
            case STRING:
            case BYTES:
            case LABEL:
            case TYPE_REFERENCE:
            case CLASS_REFERENCE:
            case NULL:
                result = this.origin.encode(object, type);
                break;
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                result = ByteBuffer.allocate(Double.BYTES)
                    .putDouble(((Number) object).doubleValue())
                    .array();
                break;
            default:
                throw new UnsupportedDataType(type);
        }
        return result;
    }

    @Override
    public Object decode(final byte[] bytes, final DataType type) {
        final Object result;
        switch (type) {
            case BOOL:
            case CHAR:
            case STRING:
            case BYTES:
            case LABEL:
            case TYPE_REFERENCE:
            case CLASS_REFERENCE:
            case NULL:
                result = this.origin.decode(bytes, type);
                break;
            case BYTE:
                result = (byte) ByteBuffer.wrap(bytes).getDouble();
                break;
            case SHORT:
                result = (short) ByteBuffer.wrap(bytes).getDouble();
                break;
            case INT:
                result = (int) ByteBuffer.wrap(bytes).getDouble();
                break;
            case LONG:
                result = (long) ByteBuffer.wrap(bytes).getDouble();
                break;
            case FLOAT:
                result = (float) ByteBuffer.wrap(bytes).getDouble();
                break;
            case DOUBLE:
                result = ByteBuffer.wrap(bytes).getDouble();
                break;
            default:
                throw new UnsupportedDataType(type);
        }
        return result;
    }
}
