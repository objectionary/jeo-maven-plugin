/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
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
     * Index of the label in the method.
     */
    private final int index;

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
     * @param index Index of the label in the method.
     * @param format Format of the directives.
     * @param identifier Identifier for the label.
     */
    public DirectivesLabel(final int index, final Format format, final String identifier) {
        this.index = index;
        this.format = format;
        this.identifier = identifier;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterable<Directive> result;
        if (Objects.isNull(this.identifier)) {
            result = new DirectivesEoObject("nop", new NumName("n", this.index).toString());
        } else {
            result = new DirectivesJeoObject(
                "label",
                new NumName("l", this.index).toString(),
                new DirectivesValue(0, this.format, this.identifier)
            );
        }
        return result.iterator();
    }
}
