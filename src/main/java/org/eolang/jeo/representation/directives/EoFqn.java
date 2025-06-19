/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

/**
 * EO fully qualified name generator.
 *
 * <p>This class generates fully qualified names for EO objects by adding
 * the standard EO namespace prefix to base names.</p>
 *
 * <p>Examples:
 * </p>
 * <ul>
 * <li>{@code seq} -> {@code Q.org.eolang.seq}</li>
 * <li>{@code bytes} -> {@code Q.org.eolang.bytes}</li>
 * <li>{@code math} -> {@code Q.org.eolang.math}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public final class EoFqn {

    /**
     * Base name.
     */
    private final String base;

    /**
     * Constructor.
     * @param base The base name to qualify
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
