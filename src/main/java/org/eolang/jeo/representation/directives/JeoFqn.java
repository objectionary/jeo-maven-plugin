/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

/**
 * Jeo fully qualified name.
 * Adds a jeo-maven-plugin package name to an object base.
 * For example,
 * - `opcode` -> `org.eolang.jeo.opcode`
 * - `representation` -> `org.eolang.jeo.representation`
 * - `int` -> `org.eolang.jeo.int`
 * @since 0.6
 * @todo #710:90min Refactor the usage of {@link JeoFqn} in the project.
 *  The solution with FQN transformation is not the best one.
 *  We create this object many times and use it in many places.
 *  All the usages are spread across the project.
 *  We need to refactor the project to use the FQN in a more centralized way.
 *  The same problem is related to {@link EoFqn}.
 */
public final class JeoFqn {

    /**
     * Base name.
     */
    private final String base;

    /**
     * Constructor.
     * @param base Base name.
     */
    public JeoFqn(final String base) {
        this.base = base;
    }

    /**
     * Get a fully qualified name.
     * @return Fully qualified name.
     */
    public String fqn() {
        return String.format("Q.jeo.%s", this.base);
    }

    @Override
    public String toString() {
        return this.fqn();
    }

}
