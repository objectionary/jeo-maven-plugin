/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.inheritance;

class SomeException extends org.eolang.inheritance.OriginalException {

    private static final long serialVersionUID = 12312123123213L;

    public SomeException(String s) {
        super(s);
    }

    public SomeException(String s, Throwable t) {
        super(s, t);
    }

}
