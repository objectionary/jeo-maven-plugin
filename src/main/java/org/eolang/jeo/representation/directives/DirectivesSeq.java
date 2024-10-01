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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.scalar.LengthOf;
import org.cactoos.scalar.Unchecked;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives sequence.
 * @since 0.6
 */
public final class DirectivesSeq implements Iterable<Directive> {

    /**
     * Name of the sequence.
     */
    private final String name;

    /**
     * Directives.
     */
    private final List<? extends Iterable<Directive>> directives;

    /**
     * Constructor.
     * @param name Name of the sequence.
     * @param elements Elements to wrap.
     */
    public DirectivesSeq(final String name, final List<? extends Iterable<Directive>> elements) {
        this.name = name;
        this.directives = elements;
    }

    /**
     * Constructor.
     * @param elements Elements to wrap.
     */
    @SafeVarargs
    DirectivesSeq(final Iterable<Directive>... elements) {
        this("@", elements);
    }

    /**
     * Constructor.
     *
     * @param name Name of the sequence.
     * @param elements Elements to wrap.
     */
    @SafeVarargs
    private DirectivesSeq(final String name, final Iterable<Directive>... elements) {
        this(name, Arrays.asList(elements));
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesEoObject(
            String.format("seq%d", this.size()),
            this.name,
            this.stream().map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }

    /**
     * Size of the sequence.
     * @return Size.
     */
    private long size() {
        return this.stream().count();
    }

    /**
     * Stream of directives.
     * @return Stream of directives.
     */
    private Stream<Directives> stream() {
        return this.directives.stream()
            .filter(Objects::nonNull)
            .filter(dirs -> new Unchecked<>(new LengthOf(dirs)).value() > 0)
            .map(Directives::new);
    }
}
