/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
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
