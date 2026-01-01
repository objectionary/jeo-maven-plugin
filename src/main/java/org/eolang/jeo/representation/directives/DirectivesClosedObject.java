/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * EO closed object directives.
 * <p>
 * It is the simples representation of an EO object as a closes object which means it
 * has 'as' ans 'base' attributes and might have 'name' attribute.
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1130">see for more info</a>
 * </p>
 * @since 0.11.0
 */
public final class DirectivesClosedObject implements Iterable<Directive> {

    /**
     * The name of the closed object.
     */
    private final String base;

    /**
     * Attribute 'as' of the closed object.
     * @checkstyle MemberNameCheck (2 lines)
     */
    private final String as;

    /**
     * Attribute 'name' of the closed object.
     */
    private final String name;

    /**
     * Inner components of the closed object.
     */
    private final Iterable<Directive> internal;

    /**
     * Constructor.
     * @param base The 'base' attribute of the closed object.
     * @param internal Inner components of the closed object.
     */
    DirectivesClosedObject(final String base, final Iterable<Directive> internal) {
        this(base, "", internal);
    }

    /**
     * Constructor.
     * @param base The 'base' attribute of the closed object.
     * @param as The 'as' attribute of the closed object.
     * @param internal Inner components of the closed object.
     * @checkstyle ParameterNameCheck (5 lines)
     */
    DirectivesClosedObject(
        final String base, final String as, final Iterable<Directive> internal) {
        this(base, as, "", internal);
    }

    /**
     * Constructor.
     * @param base The 'base' attribute of the closed object.
     * @param as The 'as' attribute of the closed object.
     * @param name The 'name' attribute of the closed object.
     * @param internal Inner components of the closed object.
     * @checkstyle ParameterNameCheck (5 lines)
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    DirectivesClosedObject(
        final String base,
        final String as,
        final String name,
        final Iterable<Directive> internal
    ) {
        this.base = base;
        this.as = as;
        this.name = name;
        this.internal = internal;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives().add("o");
        if (!this.as.isEmpty()) {
            directives.attr("as", this.as);
        }
        if (!this.name.isEmpty()) {
            directives.attr("name", this.name);
        }
        directives.attr("base", this.base);
        if (this.internal.iterator().hasNext()) {
            directives.append(this.internal);
        }
        directives.up();
        return directives.iterator();
    }
}
