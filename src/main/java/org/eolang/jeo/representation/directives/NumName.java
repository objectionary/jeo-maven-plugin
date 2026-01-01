/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

/**
 * Numbered name.
 * @since 0.14.0
 */
public final class NumName {

    /**
     * Suffix for the name.
     */
    private final String suffix;

    /**
     * Number for the name.
     */
    private final long number;

    /**
     * Constructor.
     * @param suffix Suffix for the name.
     * @param number Number for the name.
     */
    public NumName(final String suffix, final long number) {
        this.suffix = suffix;
        this.number = number;
    }

    @Override
    public String toString() {
        return String.format("%s%d", this.suffix, this.number);
    }
}
