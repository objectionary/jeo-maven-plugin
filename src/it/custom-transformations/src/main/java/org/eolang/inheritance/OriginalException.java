/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.inheritance;

class OriginalException extends RuntimeException {

    public OriginalException(String message) {
        super(message);
    }

    public OriginalException(String message, Throwable cause) {
        super(message, cause);
    }

}
