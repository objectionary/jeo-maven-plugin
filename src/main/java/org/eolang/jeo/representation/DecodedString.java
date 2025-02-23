/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Simple string that might be encoded.
 * @since 0.6
 */
public final class DecodedString {

    /**
     * Original string to encode.
     */
    private final String original;

    /**
     * Constructor.
     * @param decoded Decoded string.
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
