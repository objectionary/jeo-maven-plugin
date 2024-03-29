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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Tuple representation of a Java array as Xembly directives.
 */
public final class DirectivesTypedTuple implements Iterable<Directive> {

    private final Class<?> type;

    private final Object[] values;

    public DirectivesTypedTuple(final Class<?> type, final Object... values) {
        this.type = type;
        this.values = values;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives tuple = new Directives()
            .add("o")
            .attr("base", "tuple")
            .attr("star", "");
        tuple.append(new DirectivesData("type", this.type.getName()));
        Arrays.stream(this.values)
            .filter(Objects::nonNull)
            .map(DirectivesData::new)
            .forEach(tuple::append);
        tuple.up();
        return tuple.iterator();
    }
}
