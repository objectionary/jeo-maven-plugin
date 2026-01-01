/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
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
     * The 'as' attribute of the object.
     * @checkstyle MemberNameCheck (2 lines)
     */
    private final String as;

    /**
     * Inner components.
     */
    private final List<Directives> inner;

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
        this(base, name, "", inner);
    }

    /**
     * Constructor.
     * @param base The base of the object.
     * @param name The name of the object.
     * @param as The 'as' attribute of the object.
     * @param inner Inner components.
     * @checkstyle ParameterNumberCheck (5 lines)
     * @checkstyle ParameterNameCheck (5 lines)
     */
    public DirectivesJeoObject(
        final String base,
        final String name,
        final String as,
        final Directives... inner
    ) {
        this(base, name, as, Arrays.asList(inner));
    }

    /**
     * Constructor.
     * @param base The base of the object.
     * @param name The name of the object.
     * @param as The 'as' attribute of the object.
     * @param inner Inner components.
     * @checkstyle ParameterNameCheck (5 lines)
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesJeoObject(
        final String base, final String name, final String as, final List<Directives> inner
    ) {
        this.base = base;
        this.name = name;
        this.as = as;
        this.inner = inner;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesDelegateObject(
            new JeoFqn(this.base).fqn(),
            this.as,
            this.name,
            this.inner.stream().reduce(new Directives(), Directives::append)
        ).iterator();
    }
}
