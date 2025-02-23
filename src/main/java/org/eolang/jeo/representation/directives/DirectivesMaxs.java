/*
 * The MIT License (MIT)
 *
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
        this(0, 0);
    }

    /**
     * Constructor.
     *
     * @param stack Max stack size.
     * @param locals Max locals size.
     */
    public DirectivesMaxs(final int stack, final int locals) {
        this.stack = stack;
        this.locals = locals;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "maxs",
            "maxs",
            new DirectivesValue(this.stack),
            new DirectivesValue(this.locals)
        ).iterator();
    }
}
