/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectweb.asm.Handle;

/**
 * Bytecode handler.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
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
     * @param tag Tag.
     * @param owner Owner.
     * @param name Name.
     * @param descriptor Descriptor.
     * @param interf Is it an interface?
     * @checkstyle ParameterNumberCheck (5 lines)
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
     * @return Handler.
     */
    public Handle asHandle() {
        return new Handle(this.tag, this.owner, this.name, this.descriptor, this.interf);
    }

}
