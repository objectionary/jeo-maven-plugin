/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.io.ByteArrayOutputStream;

/**
 * Java string encoded as UTF-8, lone surrogates included.
 *
 * <p>{@link String#getBytes(java.nio.charset.Charset)} replaces a surrogate
 * code unit that has no pair with a question mark, so such a string doesn't
 * survive the round trip through XMIR. Here every code point, a lone
 * surrogate included, is written with the ordinary UTF-8 layout, and
 * {@link SurrogateBytes} reads it back.</p>
 *
 * @since 0.18.0
 */
public final class SurrogateText {

    /**
     * The text to encode.
     */
    private final String text;

    /**
     * Constructor.
     *
     * @param text The text to encode
     */
    public SurrogateText(final String text) {
        this.text = text;
    }

    /**
     * Encode.
     *
     * @return UTF-8 bytes
     */
    public byte[] bytes() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        int idx = 0;
        while (idx < this.text.length()) {
            final int code = this.text.codePointAt(idx);
            SurrogateText.write(out, code);
            idx += Character.charCount(code);
        }
        return out.toByteArray();
    }

    private static void write(final ByteArrayOutputStream out, final int code) {
        if (code < 0x80) {
            out.write(code);
        } else if (code < 0x800) {
            out.write(0xC0 | code >> 6);
            out.write(0x80 | code & 0x3F);
        } else if (code < 0x10000) {
            out.write(0xE0 | code >> 12);
            out.write(0x80 | code >> 6 & 0x3F);
            out.write(0x80 | code & 0x3F);
        } else {
            out.write(0xF0 | code >> 18);
            out.write(0x80 | code >> 12 & 0x3F);
            out.write(0x80 | code >> 6 & 0x3F);
            out.write(0x80 | code & 0x3F);
        }
    }
}
