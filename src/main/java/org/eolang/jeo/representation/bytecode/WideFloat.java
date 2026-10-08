/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

/**
 * Bits of the double that holds a float.
 *
 * <p>A float NaN is moved into the double bit by bit, since the usual widening
 * sets the quiet bit and turns a signaling NaN into a quiet one.</p>
 *
 * @since 0.18.0
 */
final class WideFloat {

    /**
     * The float.
     */
    private final float value;

    /**
     * Constructor.
     *
     * @param value The float
     */
    WideFloat(final float value) {
        this.value = value;
    }

    /**
     * Bits of the double.
     *
     * @return Bits
     */
    long bits() {
        final long bits;
        if (Float.isNaN(this.value)) {
            final int raw = Float.floatToRawIntBits(this.value);
            bits = (long) (raw >>> 31) << 63 | 0x7FF0_0000_0000_0000L
                | (long) (raw & 0x007F_FFFF) << 29;
        } else {
            bits = Double.doubleToRawLongBits(this.value);
        }
        return bits;
    }
}
