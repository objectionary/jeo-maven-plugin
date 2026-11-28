/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

/**
 * Compatibility wrapper for {@link UrlEncoder}.
 *
 * @since 0.6.0
 * @deprecated Use {@link UrlEncoder} instead.
 */
@Deprecated
public final class DecodedString {

    /**
     * Delegate for the original operation.
     */
    private final UrlEncoder delegate;

    /**
     * Constructor.
     *
     * @param decoded The string to URL-encode
     */
    public DecodedString(final String decoded) {
        this.delegate = new UrlEncoder(decoded);
    }

    /**
     * Encode the string.
     *
     * @return The encoded string
     */
    public String encode() {
        return this.delegate.encode();
    }
}
