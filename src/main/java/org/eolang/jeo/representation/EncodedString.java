/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * A utility class for decoding URL-encoded strings.
 *
 * <p>This class wraps an encoded string and provides functionality to decode it
 * using UTF-8 URL decoding. It handles decoding exceptions internally and
 * throws IllegalStateException if decoding fails.</p>
 * @since 0.6.0
 */
public final class EncodedString {

    /**
     * Original string to decode.
     */
    private final String original;

    /**
     * Constructor.
     * @param encoded The URL-encoded string to be decoded
     */
    public EncodedString(final String encoded) {
        this.original = encoded;
    }

    /**
     * Decode the string.
     * @return Decoded string.
     */
    public String decode() {
        try {
            return URLDecoder.decode(this.original, StandardCharsets.UTF_8.name());
        } catch (final UnsupportedEncodingException exception) {
            throw new IllegalStateException(
                String.format("Failed to decode the '%s' using URLDecoder", this.original),
                exception
            );
        }
    }
}
