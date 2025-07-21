/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for an EO object that delegates to another object.
 * <p>
 *     Alternative representations of the same idea:
 *     - {@link DirectivesClosedObject} - a closed object with a base attribute,
 *     - {@link DirectivesAbsractObject} - an abstract object with a base attribute,
 * </p>
 * @since 0.12.0
 */
public final class DirectivesDelegateObject implements Iterable<Directive> {

    /**
     * The name of the abstract object.
     */
    private final String base;

    /**
     * Attribute 'as' of the abstract object.
     * @checkstyle MemberNameCheck (2 lines)
     */
    private final String as;

    /**
     * Attribute 'name' of the abstract object.
     */
    private final String name;

    /**
     * Inner components of the abstract object.
     */
    private final Iterable<Directive> internal;

    /**
     * Constructor.
     * @param base The 'base' attribute of the abstract object.
     * @param internal Inner components of the abstract object.
     */
    DirectivesDelegateObject(final String base, final Iterable<Directive> internal) {
        this(base, "", internal);
    }

    /**
     * Constructor.
     * @param base The 'base' attribute of the abstract object.
     * @param as The 'as' attribute of the abstract object.
     * @param directives Inner components of the abstract object.
     * @checkstyle ParameterNameCheck (5 lines)
     */
    DirectivesDelegateObject(
        final String base,
        final String as,
        final Iterable<Directive> directives
    ) {
        this(base, as, "", directives);
    }

    /**
     * Constructor.
     * @param base The 'base' attribute of the abstract object.
     * @param as The 'as' attribute of the abstract object.
     * @param name The 'name' attribute of the abstract object.
     * @param internal Inner components of the abstract object.
     * @checkstyle ParameterNameCheck (5 lines)
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesDelegateObject(
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
        directives.append(new DirectivesSimpleDelegate(this.base));
        if (this.internal.iterator().hasNext()) {
            directives.append(this.internal);
        }
        directives.up();
        return directives.iterator();
    }
}
