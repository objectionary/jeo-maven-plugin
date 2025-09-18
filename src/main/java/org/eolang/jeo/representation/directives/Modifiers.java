/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

/**
 * Modifiers that can be applied to classes, methods, or fields.
 * Like public, private, static, final, etc.
 * @since 0.15.0
 */
final class Modifiers {

    /**
     * Modifiers value as integer.
     */
    private final int all;

    /**
     * Constructor.
     * @param modifier Modifier value as integer.
     */
    Modifiers(final int modifier) {
        this.all = modifier;
    }

    /**
     * Checks if the given flag is set.
     * @param flag Flag to check.
     * @return True if the flag is set, false otherwise.
     * @checkstyle MethodNameCheck (3 lines)
     */
    @SuppressWarnings("PMD.ShortMethodName")
    public boolean is(final int flag) {
        return (this.all & flag) != 0;
    }
}
