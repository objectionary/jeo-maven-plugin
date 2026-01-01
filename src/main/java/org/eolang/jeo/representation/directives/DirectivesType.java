/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Directives for Type object.
 * @since 0.11.0
 */
public final class DirectivesType implements Iterable<Directive> {

    /**
     * Ordered index.
     */
    private final int index;

    /**
     * The format of the directives.
     */
    private final Format format;

    /**
     * ASM Type object.
     */
    private final Type type;

    /**
     * Constructor.
     * @param index Ordered index
     * @param format The format of the directives.
     * @param type ASM Type object
     */
    public DirectivesType(final int index, final Format format, final Type type) {
        this.index = index;
        this.format = format;
        this.type = type;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "type",
            new NumName("t", this.index).toString(),
            new DirectivesValue(0, this.format, this.type.getDescriptor())
        ).iterator();
    }
}
