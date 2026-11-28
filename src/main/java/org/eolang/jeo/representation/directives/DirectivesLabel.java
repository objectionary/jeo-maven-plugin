/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives of a Java label in EO language.
 *
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
     * Type annotations of local variable scopes opened by the label.
     */
    private final DirectivesTypeAnnotations types;

    /**
     * Default constructor.
     *
     * @param index Index of the label in the method
     * @param format Format of the directives
     * @param identifier Identifier for the label
     */
    public DirectivesLabel(final int index, final Format format, final String identifier) {
        this(index, format, identifier, new DirectivesTypeAnnotations());
    }

    /**
     * Constructor.
     *
     * @param index Index of the label in the method
     * @param format Format of the directives
     * @param identifier Identifier for the label
     * @param types Type annotations of local variable scopes opened by the label
     */
    public DirectivesLabel(
        final int index,
        final Format format,
        final String identifier,
        final DirectivesTypeAnnotations types
    ) {
        this.index = index;
        this.format = format;
        this.identifier = identifier;
        this.types = types;
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
                this.children()
            );
        }
        return result.iterator();
    }

    private List<Directives> children() {
        final List<Iterable<Directive>> children = new ArrayList<>(0);
        children.add(new DirectivesValue(0, this.format, this.identifier));
        if (!this.types.isEmpty()) {
            children.add(this.types);
        }
        return children.stream().map(Directives::new).collect(Collectors.toList());
    }
}
