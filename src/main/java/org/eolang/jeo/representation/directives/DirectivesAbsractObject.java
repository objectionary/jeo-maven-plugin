/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives that represent an abstract EO object.
 * <p>
 * This is similar to {@link DirectivesClosedObject}, the main difference is that
 * it keeps the 'base' attribute as a first EO attribute, instead of a XML attribute (base=x).
 * </p>
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1130">read more</a>
 * @since 0.11.0
 */
public final class DirectivesAbsractObject implements Iterable<Directive> {

    /**
     * Format of the directives.
     */
    private final Format format;

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
    DirectivesAbsractObject(final String base, final Iterable<Directive> internal) {
        this(base, "", internal);
    }

    /**
     * Constructor.
     * @param base The 'base' attribute of the abstract object.
     * @param as The 'as' attribute of the abstract object.
     * @param directives Inner components of the abstract object.
     * @checkstyle ParameterNameCheck (5 lines)
     */
    DirectivesAbsractObject(
        final String base,
        final String as,
        final Iterable<Directive> directives
    ) {
        this(new Format(), base, as, "", directives);
    }

    /**
     * Constructor.
     * @param format Format of the directives.
     * @param base The 'base' attribute of the abstract object.
     * @param as The 'as' attribute of the abstract object.
     * @param name The 'name' attribute of the abstract object.
     * @param internal Inner components of the abstract object.
     * @checkstyle ParameterNameCheck (10 lines)
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public DirectivesAbsractObject(
        final Format format,
        final String base,
        final String as,
        final String name,
        final Iterable<Directive> internal
    ) {
        this.format = format;
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
        directives.append(new DirectivesValue(this.format, "base", this.base));
        if (this.internal.iterator().hasNext()) {
            directives.append(this.internal);
        }
        directives.up();
        return directives.iterator();
    }
}
