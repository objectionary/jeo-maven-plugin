/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;

/**
 * Directives for attributes.
 * @since 0.4
 */
public final class DirectivesAttributes implements Iterable<Directive> {

    /**
     * Name.
     */
    private final String name;

    /**
     * Attributes.
     */
    private final List<Iterable<Directive>> attributes;

    public DirectivesAttributes(final String name, final List<Iterable<Directive>> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    /**
     * Constructor.
     */
    public DirectivesAttributes() {
        this(new ArrayList<>(0));
    }

    /**
     * Constructor.
     * @param attributes Attributes.
     */
    @SafeVarargs
    DirectivesAttributes(final Iterable<Directive>... attributes) {
        this(Arrays.asList(attributes));
    }

    /**
     * Constructor.
     * @param attributes Separate attributes.
     */
    private DirectivesAttributes(final List<Iterable<Directive>> attributes) {
        this("attributes", attributes);
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesSeq(this.name, this.attributes).iterator();
    }
}
