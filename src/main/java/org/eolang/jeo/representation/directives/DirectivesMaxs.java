/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

/**
 * Max stack and locals.
 * @since 0.3
 */
public final class DirectivesMaxs implements Iterable<Directive> {

    /**
     * Undefined value.
     * Need to be recomputed.
     */
    private static final int UNDEFINED = -1;

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * Max stack size.
     */
    private final int stack;

    /**
     * Max locals size.
     */
    private final int locals;

    /**
     * Constructor.
     */
    public DirectivesMaxs() {
        this(new Format(), DirectivesMaxs.UNDEFINED, DirectivesMaxs.UNDEFINED);
    }

    /**
     * Constructor.
     *
     * @param format Format of the directives.
     * @param stack Max stack size.
     * @param locals Max locals size.
     */
    public DirectivesMaxs(final Format format, final int stack, final int locals) {
        this.format = format;
        this.stack = stack;
        this.locals = locals;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "maxs",
            "maxs",
            new DirectivesValue(this.format, new NumName("m", 1), this.stack),
            new DirectivesValue(this.format, new NumName("m", 2), this.locals)
        ).iterator();
    }
}
