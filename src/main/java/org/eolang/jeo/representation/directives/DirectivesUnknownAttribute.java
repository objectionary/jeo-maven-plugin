/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

/**
 * Unknown attribute directives.
 * @since 0.15.0
 */
public final class DirectivesUnknownAttribute  implements Iterable<Directive> {

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
     * Constructor.
     * @param format Format of the directives.
     * @param index Index of the attribute.
     * @param type Type of the attribute.
     * @param data Data of the attribute.
     * @checkstyle ParameterNumber (10 lines)
     */
    public DirectivesUnknownAttribute(
        final Format format,
        final int index,
        final String type,
        final byte[] data
    ) {
        this.format = format;
        this.index = index;
        this.type = type;
        this.data = data.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "unknown-attribute",
            String.format("a%d", this.index),
            new DirectivesValue(this.format, "type", this.type),
            new DirectivesValue(this.format, "data", this.data)
        ).iterator();
    }
}
