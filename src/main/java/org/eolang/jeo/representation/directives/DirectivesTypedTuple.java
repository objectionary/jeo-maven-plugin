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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Tuple representation of a Java array as Xembly directives.
 */
public final class DirectivesTypedTuple implements Iterable<Directive> {

    private final String name;

    private final Class<?> type;

    private final List<DirectivesData> values;

    public DirectivesTypedTuple(final String name, int[] values) {
        this(name, int[].class, Arrays.stream(values).mapToObj(i -> i));
    }

    public DirectivesTypedTuple(final String name, long[] values) {
        this(name, long[].class, Arrays.stream(values).mapToObj(i -> i));
    }

    public DirectivesTypedTuple(final String name, double[] values) {
        this(name, double[].class, Arrays.stream(values).mapToObj(i -> i));
    }

    public DirectivesTypedTuple(final String name, float[] values) {
        this(
            name,
            float[].class,
            IntStream.range(0, values.length).mapToObj(i -> values[i])
        );
    }

    public DirectivesTypedTuple(final String name, boolean[] values) {
        this(
            name,
            boolean[].class,
            IntStream.range(0, values.length).mapToObj(i -> values[i])
        );
    }

    public DirectivesTypedTuple(final String name, char[] values) {
        this(
            name,
            char[].class,
            IntStream.range(0, values.length).mapToObj(i -> values[i])
        );
    }

    public DirectivesTypedTuple(final String name, byte[] values) {
        this(
            name,
            byte[].class,
            IntStream.range(0, values.length).mapToObj(i -> values[i])
        );
    }

    public DirectivesTypedTuple(final String name, short[] values) {
        this(
            name,
            short[].class,
            IntStream.range(0, values.length).mapToObj(i -> values[i])
        );
    }

    public DirectivesTypedTuple(final String name, Object[] values) {
        this(
            name,
            values.getClass(),
            IntStream.range(0, values.length).mapToObj(i -> values[i])
        );
    }

    public DirectivesTypedTuple(
        final String name,
        final Class<?> type,
        Stream<Object> values
    ) {
        this(name, type, values.map(DirectivesData::new).collect(Collectors.toList()));
    }


    public DirectivesTypedTuple(
        final String name,
        final Class<?> type,
        final List<DirectivesData> values
    ) {
        this.name = name;
        this.type = type;
        this.values = values;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives tuple = new Directives()
            .add("o")
            .attr("base", "tuple")
            .attr("star", "");
        if (!this.name.isEmpty()) {
            tuple.attr("name", this.name);
        }
        tuple.append(new DirectivesData("type", this.type.getName()));
        this.values.forEach(tuple::append);
        tuple.up();
        return tuple.iterator();
    }
}
