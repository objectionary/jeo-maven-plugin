/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Unknown attribute directives.
 *
 * @since 0.15.0
 */
public final class DirectivesUnknownAttribute implements Iterable<Directive> {

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * Index of the attribute.
     */
    private final int index;

    /**
     * Type of the attribute.
     */
    private final String type;

    /**
     * Data of the attribute.
     */
    private final byte[] data;

    /**
     * Whether this attribute belongs to a method's Code attribute.
     */
    private final boolean code;

    /**
     * Constructor.
     *
     * @param format Format of the directives
     * @param index Index of the attribute
     * @param type Type of the attribute
     * @param data Data of the attribute
     */
    public DirectivesUnknownAttribute(
        final Format format,
        final int index,
        final String type,
        final byte[] data
    ) {
        this(format, index, type, data, false);
    }

    /**
     * Constructor.
     *
     * @param format Format of the directives
     * @param index Index of the attribute
     * @param type Type of the attribute
     * @param data Data of the attribute
     * @param code Whether the attribute belongs to a Code attribute
     */
    public DirectivesUnknownAttribute(
        final Format format,
        final int index,
        final String type,
        final byte[] data,
        final boolean code
    ) {
        this.format = format;
        this.index = index;
        this.type = type;
        this.data = data.clone();
        this.code = code;
    }

    @Override
    public Iterator<Directive> iterator() {
        final List<Directives> inner = new ArrayList<>(0);
        inner.add(new Directives(new DirectivesValue(this.format, "type", this.type)));
        inner.add(new Directives(new DirectivesValue(this.format, "data", this.data)));
        if (this.code) {
            inner.add(new Directives(new DirectivesValue(this.format, "code", true)));
        }
        return new DirectivesJeoObject(
            "unknown-attribute",
            String.format("a%d", this.index),
            inner
        ).iterator();
    }
}
