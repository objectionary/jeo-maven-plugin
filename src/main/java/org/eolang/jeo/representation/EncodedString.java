/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

/**
 * Compatibility wrapper for {@link UrlDecoder}.
 *
 * @since 0.6.0
 * @deprecated Use {@link UrlDecoder} instead.
 */
@Deprecated
public final class EncodedString {

    /**
     * Delegate for the original operation.
     */
    private final UrlDecoder delegate;

    /**
     * Constructor.
     *
     * @param encoded The URL-encoded string
     */
    public EncodedString(final String encoded) {
        this.delegate = new UrlDecoder(encoded);
    }

    /**
     * Decode the string.
     *
     * @return The decoded string
     */
    public String decode() {
        return this.delegate.decode();
    }
}
