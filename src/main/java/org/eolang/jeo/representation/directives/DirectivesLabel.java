/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Objects;
import org.xembly.Directive;

/**
 * Directives of a Java label in EO language.
 * @since 0.12.0
 */
public final class DirectivesLabel implements Iterable<Directive> {

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * Simple string identifier.
     */
    private final String identifier;

    /**
     * Default constructor.
     * @param format Format of the directives.
     * @param identifier Identifier for the label.
     */
    public DirectivesLabel(final Format format, final String identifier) {
        this.format = format;
        this.identifier = identifier;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterable<Directive> result;
        if (Objects.isNull(this.identifier)) {
            result = new DirectivesEoObject("nop", new RandName("n").toString());
        } else {
            result = new DirectivesJeoObject(
                "label",
                new RandName("l").toString(),
                new DirectivesValue(this.format, this.identifier)
            );
        }
        return result.iterator();
    }
}
