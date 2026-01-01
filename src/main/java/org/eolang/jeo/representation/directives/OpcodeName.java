/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.concurrent.atomic.AtomicInteger;
import org.eolang.jeo.representation.OpcodeDictionary;

/**
 * Opcode name.
 * The name of bytecode instruction. The name combined with unique number in order to
 * avoid name collisions.
 * @since 0.1.0
 */
public final class OpcodeName {

    /**
     * Opcode names.
     */
    private static final OpcodeDictionary NAMES = new OpcodeDictionary();

    /**
     * Default counter.
     */
    private static final AtomicInteger DEFAULT = new AtomicInteger(0);

    /**
     * Bytecode operation code.
     */
    private final int opcode;

    /**
     * Opcode counter.
     */
    private final AtomicInteger counter;

    /**
     * Constructor.
     * @param opcode Bytecode operation code.
     */
    public OpcodeName(final int opcode) {
        this(opcode, OpcodeName.DEFAULT);
    }

    /**
     * Constructor.
     * @param opcode Bytecode operation code.
     * @param counter Opcode counter.
     */
    OpcodeName(final int opcode, final AtomicInteger counter) {
        this.opcode = opcode;
        this.counter = counter;
    }

    /**
     * Get simplified opcode name without counter.
     * @return Simplified opcode name.
     */
    public String simplified() {
        return OpcodeName.NAMES.name(this.opcode);
    }

    /**
     * Get string representation of a bytecode.
     * @return String representation of a bytecode.
     */
    String asString() {
        return String.format(
            "%s-%X", OpcodeName.NAMES.name(this.opcode), this.counter.incrementAndGet()
        );
    }
}
