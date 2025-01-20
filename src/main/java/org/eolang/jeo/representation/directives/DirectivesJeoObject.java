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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives that represent a pure JEO object.
 * Similar to {@link DirectivesEoObject},
 * but for objects that are parts of the JEO XMIR representation.
 * @since 0.6
 * @todo #946:60min Generate Documentation from The Source Code.
 *  We might try to generate documentation about jeo objects from the source code.
 *  See the "Full List of Jeo Objects" section in the README file.
 *  This was originally suggested here:
 *  <a href="https://github.com/objectionary/jeo-maven-plugin/pull/961#discussion_r1908445938">
 *      PR comment.
 *  </a>.
 */
public final class DirectivesJeoObject implements Iterable<Directive> {

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
     * @param inner Inner components.
     */
    @SafeVarargs
    public DirectivesJeoObject(final String base, final Iterable<Directive>... inner) {
        this(base, Arrays.stream(inner).map(Directives::new).collect(Collectors.toList()));
    }

    /**
     * Constructor.
     * @param base The base of the object.
     * @param inner Inner components.
     */
    public DirectivesJeoObject(final String base, final Directives... inner) {
        this(base, Arrays.asList(inner));
    }

    /**
     * Constructor.
     * @param base The base of the object.
     * @param inner Inner components.
     */
    public DirectivesJeoObject(final String base, final List<Directives> inner) {
        this(base, "", inner);
    }

    /**
     * Constructor.
     * @param base The base of the object.
     * @param name The name of the object.
     * @param inner Inner components.
     */
    @SafeVarargs
    public DirectivesJeoObject(
        final String base, final String name, final Iterable<Directive>... inner
    ) {
        this(base, name, Arrays.stream(inner).map(Directives::new).collect(Collectors.toList()));
    }

    /**
     * Constructor.
     * @param base The base of the object.
     * @param name The name of the object.
     * @param inner Inner components.
     */
    public DirectivesJeoObject(final String base, final String name, final Directives... inner) {
        this(base, name, Arrays.asList(inner));
    }

    /**
     * Constructor.
     * @param base The base of the object.
     * @param name The name of the object.
     * @param inner Inner components.
     */
    public DirectivesJeoObject(final String base, final String name, final List<Directives> inner) {
        this.base = base;
        this.name = name;
        this.inner = inner;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives().add("o")
            .attr("base", new JeoFqn(this.base).fqn());
        if (!this.name.isEmpty()) {
            directives.attr("as", this.name);
        }
        return directives
            .append(this.inner.stream().reduce(new Directives(), Directives::append))
            .up()
            .iterator();
    }
}
