/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Tuple representation as Xembly directives.
 *
 * @since 0.1.0
 */
public final class DirectivesTuple implements Iterable<Directive> {

    /**
     * Tuple name.
     */
    private final String name;

    /**
     * Tuple values.
     */
    private final Object[] values;

    /**
     * Constructor.
     * @param name Tuple name.
     * @param values Tuple values.
     * @param <T> Value type.
     */
    @SuppressWarnings("unchecked")
    public <T> DirectivesTuple(
        final String name,
        final T... values
    ) {
        this.name = name;
        this.values = values.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives tuple = new Directives()
            .add("o")
            .attr("base", "tuple")
            .attr("star", "")
            .attr("name", this.name);
        for (final Object exception : this.values) {
            tuple.append(new DirectivesData(exception));
        }
        tuple.up();
        return tuple.iterator();
    }
}
