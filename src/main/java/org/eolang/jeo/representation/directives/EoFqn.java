/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

/**
 * Eolang fully qualified name.
 * Adds a fully qualified name to an object base.
 * For example,
 * - `seq` -> `org.eolang.seq`
 * - `bytes` -> `org.eolang.bytes`
 * - `math` -> `org.eolang.math`
 * @since 0.6
 */
public final class EoFqn {

    /**
     * Base name.
     */
    private final String base;

    /**
     * Constructor.
     * @param base Base name.
     */
    public EoFqn(final String base) {
        this.base = base;
    }

    /**
     * Get a fully qualified name.
     * @return Fully qualified name.
     */
    public String fqn() {
        return String.format("Q.org.eolang.%s", this.base);
    }

    @Override
    public String toString() {
        return this.fqn();
    }
}
