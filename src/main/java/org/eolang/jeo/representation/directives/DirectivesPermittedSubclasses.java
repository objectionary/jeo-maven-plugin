/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;

/**
 * Directives for PermittedSubclasses attribute.
 * @since 0.14.0
 */
public final class DirectivesPermittedSubclasses implements Iterable<Directive> {

    /**
     * Directive format.
     */
    private final Format format;

    /**
     * All permitted subclasses.
     */
    private final List<String> subclasses;

    /**
     * Constructor.
     * @param format Directive format.
     * @param subclasses All permitted subclasses.
     */
    public DirectivesPermittedSubclasses(final Format format, final List<String> subclasses) {
        this.format = format;
        this.subclasses = subclasses;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String base = "permitted-subclasses";
        return new DirectivesJeoObject(
            base,
            base,
            new DirectivesValues(this.format, "subclasses", this.subclasses.toArray())
        ).iterator();
    }
}
