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
 * Xml directives for label entry in byrecode.
 * @since 0.1
 */
public final class DirectivesLabel implements Iterable<Directive> {

    /**
     * Bytecode label.
     */
    private final String label;

    /**
     * Label name.
     */
    private final String name;

    /**
     * Constructor.
     * @param label Bytecode label.
     */
    public DirectivesLabel(final String label) {
        this(label, "");
    }

    /**
     * Constructor.
     * @param label Bytecode label.
     * @param name Label name.
     */
    private DirectivesLabel(final String label, final String name) {
        this.label = label;
        this.name = name;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesValue(this.name, this.label).iterator();
    }
}
