/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

/**
 * Directives for NestHost attribute.
 * @since 0.14.0
 */
public final class DirectivesNestHost implements Iterable<Directive> {

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * Host class internal name.
     */
    private final String host;

    /**
     * Constructor.
     * @param format Format of the directives.
     * @param host Host class internal name.
     */
    public DirectivesNestHost(final Format format, final String host) {
        this.format = format;
        this.host = host;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String base = "nest-host";
        return new DirectivesJeoObject(
            base,
            base,
            new DirectivesValue(this.format, "host", this.host)
        ).iterator();
    }
}
