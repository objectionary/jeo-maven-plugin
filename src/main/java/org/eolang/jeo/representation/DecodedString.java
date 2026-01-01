/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * A utility class for encoding strings using URL encoding.
 *
 * <p>This class wraps a decoded string and provides functionality to encode it
 * using UTF-8 URL encoding. It handles encoding exceptions internally and
 * throws IllegalStateException if encoding fails.</p>
 * @since 0.6.0
 */
public final class DecodedString {

    /**
     * Original string to encode.
     */
    private final String original;

    /**
     * Constructor.
     * @param decoded The original decoded string to be encoded
     */
    public DecodedString(final String decoded) {
        this.original = decoded;
    }

    /**
     * Encode the string.
     * @return Encoded string.
     */
    public String encode() {
        try {
            return URLEncoder.encode(this.original, StandardCharsets.UTF_8.name());
        } catch (final UnsupportedEncodingException exception) {
            throw new IllegalStateException(
                String.format("Failed to encode the '%s' using URLEncoder", this.original),
                exception
            );
        }
    }
}
