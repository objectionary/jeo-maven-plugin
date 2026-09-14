/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Objects;
import org.objectweb.asm.Handle;

/**
 * Bytecode handler.
 *
 * @since 0.6
 */
public final class BytecodeHandler {

    /**
     * Handler tag.
     */
    private final int tag;

    /**
     * Owner.
     */
    private final String owner;

    /**
     * Name.
     */
    private final String name;

    /**
     * Descriptor.
     */
    private final String descriptor;

    /**
     * Is it an interface?
     */
    private final boolean interf;

    /**
     * Constructor.
     *
     * @param tag Tag
     * @param owner Owner
     * @param name Name
     * @param descriptor Descriptor
     * @param interf Is it an interface?
     */
    public BytecodeHandler(
        final int tag,
        final String owner,
        final String name,
        final String descriptor,
        final boolean interf
    ) {
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.interf = interf;
    }

    /**
     * Convert to a handler.
     *
     * @return Handler
     */
    public Handle asHandle() {
        return new Handle(this.tag, this.owner, this.name, this.descriptor, this.interf);
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeHandler) {
            final BytecodeHandler handler = (BytecodeHandler) other;
            result = this.tag == handler.tag
                && this.interf == handler.interf
                && Objects.equals(this.owner, handler.owner)
                && Objects.equals(this.name, handler.name)
                && Objects.equals(this.descriptor, handler.descriptor);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tag, this.owner, this.name, this.descriptor, this.interf);
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeHandler(tag=%d, owner=%s, name=%s, descriptor=%s, interf=%b)",
            this.tag, this.owner, this.name, this.descriptor, this.interf
        );
    }
}
