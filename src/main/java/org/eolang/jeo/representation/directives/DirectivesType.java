/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
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
     * The format of the directives.
     */
    private final Format format;

    /**
     * ASM Type object.
     */
    private final Type type;

    /**
     * Constructor.
     * @param format The format of the directives.
     * @param type ASM Type object
     */
    public DirectivesType(final Format format, final Type type) {
        this.format = format;
        this.type = type;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "type",
            new RandName("t").toString(),
            new DirectivesValue(this.format, this.type.getDescriptor())
        ).iterator();
    }
}
