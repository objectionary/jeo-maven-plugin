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
     * Default identifier for absent package.
     */
    private static final String ABSENT_PACKAGE = "jeo$packageless$jeo";

    /**
     * Identifier of the absent package.
     */
    private final String identifier;

    /**
     * Constructor.
     */
    public AbsentPackage() {
        this(AbsentPackage.ABSENT_PACKAGE);
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
