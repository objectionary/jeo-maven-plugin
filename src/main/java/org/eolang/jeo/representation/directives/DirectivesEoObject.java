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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives that represent a pure EO object.
 * Similar to {@link DirectivesJeoObject}, but for objects that are parts of the EO language.
 * @since 0.6
 */
public final class DirectivesEoObject implements Iterable<Directive> {

    /**
     * The base of the object.
     */
    private final String base;

    /**
     * The name of the object.
     */
    private final String name;

    /**
     * Inner components.
     */
    private final List<Directives> inner;

    /**
     * Constructor.
     * @param base The base of the object.
     */
    public DirectivesEoObject(final String base) {
        this(base, "", new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param base The base of the object.
     * @param name The name of the object.
     * @param inner Inner components.
     */
    DirectivesEoObject(final String base, final String name, final List<Directives> inner) {
        this.base = base;
        this.name = name;
        this.inner = inner;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives().add("o")
            .attr("base", new EoFqn(this.base).fqn());
        if (!this.name.isEmpty()) {
            directives.attr("name", this.name);
        }
        return directives
            .append(this.inner.stream().reduce(new Directives(), Directives::append))
            .up().iterator();
    }
}
