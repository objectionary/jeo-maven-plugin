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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

public final class DirectivesJeoObject implements Iterable<Directive> {

    private final String base;
    private final List<Directives> inner;

    @SafeVarargs
    public DirectivesJeoObject(final String base, final Iterable<Directive>... inner) {
        this(base, Arrays.stream(inner).map(Directives::new).toArray(Directives[]::new));
    }

    public DirectivesJeoObject(final String base, final Directives... inner) {
        this(base, Arrays.asList(inner));
    }

    public DirectivesJeoObject(final String base, final List<Directives> inner) {
        this.base = base;
        this.inner = inner;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives()
            .add("o")
            .attr("base", new JeoFqn("handle").fqn())
//            .append(new DirectivesValue(this.handle.getTag()))
//            .append(new DirectivesValue(this.handle.getOwner()))
//            .append(new DirectivesValue(this.handle.getName()))
//            .append(new DirectivesValue(this.handle.getDesc()))
//            .append(new DirectivesValue(this.handle.isInterface()))
            .append(this.inner.stream().reduce(new Directives(), Directives::append))
            .up()
            .iterator();
    }
}
