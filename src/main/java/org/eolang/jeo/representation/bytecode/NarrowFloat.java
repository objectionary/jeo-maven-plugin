/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

/**
 * The float held by the bits of a double, written by {@link WideFloat}.
 *
 * @since 0.18.0
 */
final class NarrowFloat {

    /**
     * Bits of the double.
     */
    private final long bits;

    /**
     * Constructor.
     *
     * @param bits Bits of the double
     */
    NarrowFloat(final long bits) {
        this.bits = bits;
    }

    /**
     * The float.
     *
     * @return The float
     */
    float value() {
        final float result;
        if ((this.bits & 0x7FF0_0000_0000_0000L) == 0x7FF0_0000_0000_0000L
            && (this.bits & 0x000F_FFFF_FFFF_FFFFL) != 0) {
            result = Float.intBitsToFloat(
                (int) (this.bits >>> 63) << 31 | 0x7F80_0000
                    | (int) (this.bits >>> 29) & 0x007F_FFFF
            );
        } else {
            result = (float) Double.longBitsToDouble(this.bits);
        }
        return result;
    }
}
