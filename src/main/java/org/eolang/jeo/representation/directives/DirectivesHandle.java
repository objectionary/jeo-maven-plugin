/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.objectweb.asm.Handle;
import org.xembly.Directive;

/**
 * Directives Handle.
 * This is the XMIR representation of the Java ASM Handle object.
 * @since 0.1
 */
public final class DirectivesHandle implements Iterable<Directive> {

    /**
     * The bytecode instruction index.
     */
    private final int index;

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * ASM Handle object.
     */
    private final Handle handle;

    /**
     * Constructor.
     * @param index The bytecode instruction index.
     * @param format Format of the directives.
     * @param handle ASM Handle object.
     */
    public DirectivesHandle(final int index, final Format format, final Handle handle) {
        this.index = index;
        this.format = format;
        this.handle = handle;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "handle",
            new NumName("h", this.index).toString(),
            new DirectivesValue(0, this.format, this.handle.getTag()),
            new DirectivesValue(1, this.format, this.handle.getOwner()),
            new DirectivesValue(2, this.format, this.handle.getName()),
            new DirectivesValue(3, this.format, this.handle.getDesc()),
            new DirectivesValue(4, this.format, this.handle.isInterface())
        ).iterator();
    }
}
