/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

/**
 * XMIR parsing exception.
 * This exception is thrown when XMIR parsing fails.
 * @since 0.6
 */
final class ParsingException extends IllegalStateException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 4711234567890L;

    /**
     * Constructor.
     * @param message Message.
     * @param cause Cause.
     */
    ParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
