/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives without comments.
 * <p>
 *     This class provides an iterable collection of directives that excludes comments.
 * </p>
 * <p>
 *     Note: In case of performance issues, we can invert this class and implement
 *     DirectivesWithComments that will add comments to the directives.
 * </p>
 * @since 0.11.0
 */
public final class DirectivesWithoutComments implements Iterable<Directive> {

    /**
     * Directives object that contains comments.
     */
    private final Directives directives;

    /**
     * Constructor.
     * @param directives Iterable of directives.
     */
    public DirectivesWithoutComments(final Iterable<Directive> directives) {
        this(new Directives(directives));
    }

    /**
     * Constructor.
     * @param directives Directives object.
     */
    private DirectivesWithoutComments(final Directives directives) {
        this.directives = directives;
    }

    @Override
    public Iterator<Directive> iterator() {
        return this.directives.xpath("//comment()").remove().iterator();
    }
}
