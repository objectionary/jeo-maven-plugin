/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

/**
 * <p>JEO fully qualified name generator.</p>
 * <p>This class generates fully qualified names for JEO-specific objects by adding
 * the JEO namespace prefix to base names.</p>
 * <p>Examples:</p>
 * <ul>
 * <li>`opcode` -> `org.eolang.jeo.opcode`</li>
 * <li>`representation` -> `org.eolang.jeo.representation`</li>
 * <li>`int` -> `org.eolang.jeo.int`</li>
 * </ul>
 * @since 0.6.0
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
     * @param base The base name to qualify
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
