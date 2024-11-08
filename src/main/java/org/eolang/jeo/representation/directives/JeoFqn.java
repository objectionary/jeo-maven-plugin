/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        return String.format("jeo.%s", this.base);
    }

    @Override
    public String toString() {
        return this.fqn();
    }

}
