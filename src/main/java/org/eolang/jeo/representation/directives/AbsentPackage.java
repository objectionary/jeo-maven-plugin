/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

/**
 * Representation of absent package.
 *
 * @since 0.15.0
 */
public final class AbsentPackage {

    /**
     * Identifier of the absent package.
     */
    private final String identifier;

    /**
     * Constructor.
     */
    public AbsentPackage() {
        this("jeo$packageless$jeo");
    }

    /**
     * Constructor with custom identifier.
     *
     * @param identifier Custom identifier
     */
    private AbsentPackage(final String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return this.identifier;
    }
}
