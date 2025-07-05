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
     * ASM Handle object.
     */
    private final Handle handle;

    /**
     * Constructor.
     * @param handle ASM Handle object.
     */
    public DirectivesHandle(final Handle handle) {
        this.handle = handle;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "handle",
            new RandName("handle").toString(),
            new DirectivesValue(this.handle.getTag()),
            new DirectivesValue(this.handle.getOwner()),
            new DirectivesValue(this.handle.getName()),
            new DirectivesValue(this.handle.getDesc()),
            new DirectivesValue(this.handle.isInterface())
        ).iterator();
    }
}
