/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;

/**
 * Record components directives.
 * @since 0.15.0
 */
public final class DirectivesRecordComponents implements Iterable<Directive> {

    /**
     * Record component index.
     */
    private final int index;

    /**
     * All the record components.
     */
    private final List<Iterable<Directive>> components;

    /**
     * Constructor.
     * @param index Index of the record component.
     * @param components All the record components.
     */
    public DirectivesRecordComponents(
        final int index,
        final List<Iterable<Directive>> components
    ) {
        this.index = index;
        this.components = components;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesOptionalSeq(
            String.format("record-components-%d", this.index),
            this.components
        ).iterator();
    }
}
