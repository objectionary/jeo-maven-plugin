/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

/**
 * UTF-8 bytes decoded into a Java string, lone surrogates included.
 *
 * <p>The standard decoder refuses a surrogate encoded on its own, which is
 * what {@link SurrogateText} writes for a code unit without a pair. Here such
 * a sequence becomes that code unit again. A byte that starts no valid
 * sequence becomes U+FFFD, as it does in the standard decoder.</p>
 *
 * @since 0.18.0
 */
public final class SurrogateBytes {

    /**
     * The bytes to decode.
     */
    private final byte[] bytes;

    /**
     * Constructor.
     *
     * @param bytes The bytes to decode
     */
    public SurrogateBytes(final byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Decode.
     *
     * @return The text
     */
    public String text() {
        final StringBuilder out = new StringBuilder(this.bytes.length);
        int idx = 0;
        while (idx < this.bytes.length) {
            final int size = this.size(idx);
            if (size == 0) {
                out.append((char) 0xFFFD);
                idx += 1;
            } else {
                out.appendCodePoint(this.code(idx, size));
                idx += size;
            }
        }
        return out.toString();
    }

    private int size(final int idx) {
        final int size = SurrogateBytes.expected(this.bytes[idx] & 0xFF);
        final int result;
        if (size > 1 && !this.continued(idx, size)) {
            result = 0;
        } else {
            result = size;
        }
        return result;
    }

    private boolean continued(final int idx, final int size) {
        boolean fine = idx + size <= this.bytes.length;
        for (int pos = 1; fine && pos < size; ++pos) {
            fine = (this.bytes[idx + pos] & 0xC0) == 0x80;
        }
        return fine;
    }

    private int code(final int idx, final int size) {
        int code = this.bytes[idx] & 0xFF >> size + 1;
        if (size == 1) {
            code = this.bytes[idx] & 0xFF;
        }
        for (int pos = 1; pos < size; ++pos) {
            code = code << 6 | this.bytes[idx + pos] & 0x3F;
        }
        return code;
    }

    private static int expected(final int first) {
        final int size;
        if (first < 0x80) {
            size = 1;
        } else if (first < 0xC0) {
            size = 0;
        } else if (first < 0xE0) {
            size = 2;
        } else if (first < 0xF0) {
            size = 3;
        } else if (first < 0xF8) {
            size = 4;
        } else {
            size = 0;
        }
        return size;
    }
}
