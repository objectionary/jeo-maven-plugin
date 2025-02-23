/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Encoded string that might be decoded.
 * @since 0.6
 */
public final class EncodedString {

    /**
     * Original string to decode.
     */
    private final String original;

    /**
     * Constructor.
     * @param encoded Encoded string.
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
                String.format("Failed to decode the '%s' using URLEncoder", this.original),
                exception
            );
        }
    }
}
