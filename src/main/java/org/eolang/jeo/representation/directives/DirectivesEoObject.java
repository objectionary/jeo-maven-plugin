/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
     * The 'as' attribute of the object.
     * @checkstyle MemberNameCheck (2 lines)
     */
    private final String as;

    /**
     * Constructor.
     * @param base The base of the object.
     * @param name The name of the object.
     * @param inner Inner components.
     */
    @SafeVarargs
    public DirectivesEoObject(
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
    public DirectivesEoObject(final String base, final String name, final List<Directives> inner) {
        this(base, name, "", inner);
    }

    /**
     * Constructor.
     * @param base The base of the object.
     * @param name The name of the object.
     * @param as The 'as' attribute of the object.
     * @param inner Inner components.
     * @checkstyle ParameterNumberCheck (10 lines)
     * @checkstyle ParameterNameCheck (10 lines)
     */
    @SafeVarargs
    public DirectivesEoObject(
        final String base,
        final String name,
        final String as,
        final Iterable<Directive>... inner
    ) {
        this(
            base,
            name,
            as,
            Arrays.stream(inner).map(Directives::new).collect(Collectors.toList())
        );
    }

    /**
     * Constructor.
     * @param base The base of the object.
     * @param name The name of the object.
     * @param as The 'as' attribute of the object.
     * @param inner Inner components.
     * @checkstyle ParameterNameCheck (10 lines)
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public DirectivesEoObject(
        final String base,
        final String name,
        final String as,
        final List<Directives> inner
    ) {
        this.base = base;
        this.name = name;
        this.as = as;
        this.inner = inner;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesClosedObject(
            new EoFqn(this.base).fqn(),
            this.as,
            this.name,
            this.inner.stream().reduce(new Directives(), Directives::append)
        ).iterator();
    }
}
