/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.nio.ByteBuffer;

/**
 * EO codec.
 * Converts primitive types to byte arrays and vice versa.
 *
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
     *
     * @param delegate Origin codec
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
            case NULL:
                result = this.origin.encode(object, type);
                break;
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case DOUBLE:
                result = ByteBuffer.allocate(Double.BYTES)
                    .putDouble(((Number) object).doubleValue())
                    .array();
                break;
            case FLOAT:
                result = ByteBuffer.allocate(Double.BYTES)
                    .putLong(this.widened(((Number) object).floatValue()))
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
                result = this.narrowed(ByteBuffer.wrap(bytes).getLong());
                break;
            case DOUBLE:
                result = ByteBuffer.wrap(bytes).getDouble();
                break;
            default:
                throw new UnsupportedDataType(type);
        }
        return result;
    }

    /**
     * Bits of the double that holds the float.
     *
     * <p>A float NaN is moved into the double bit by bit, since the usual widening
     * sets the quiet bit and changes a signaling NaN.</p>
     *
     * @param value The float
     * @return Bits of the double
     */
    private long widened(final float value) {
        final long bits;
        if (Float.isNaN(value)) {
            final int raw = Float.floatToRawIntBits(value);
            bits = (long) (raw >>> 31) << 63 | 0x7FF0_0000_0000_0000L
                | (long) (raw & 0x007F_FFFF) << 29;
        } else {
            bits = Double.doubleToRawLongBits(value);
        }
        return bits;
    }

    /**
     * The float held by the bits of the double.
     *
     * @param bits Bits of the double
     * @return The float
     */
    private float narrowed(final long bits) {
        final float result;
        if ((bits & 0x7FF0_0000_0000_0000L) == 0x7FF0_0000_0000_0000L
            && (bits & 0x000F_FFFF_FFFF_FFFFL) != 0) {
            result = Float.intBitsToFloat(
                (int) (bits >>> 63) << 31 | 0x7F80_0000 | (int) (bits >>> 29) & 0x007F_FFFF
            );
        } else {
            result = (float) Double.longBitsToDouble(bits);
        }
        return result;
    }
}
