/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesMaxs;
import org.eolang.jeo.representation.directives.Format;

/**
 * Bytecode maxs.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public final class BytecodeMaxs {

    /**
     * Undefined value.
     * Need to be recomputed.
     */
    private static final int UNDEFINED = -1;

    /**
     * Stack max size.
     */
    private final int stack;

    /**
     * Locals variables size.
     */
    private final int locals;

    /**
     * Constructor.
     */
    public BytecodeMaxs() {
        this(BytecodeMaxs.UNDEFINED, BytecodeMaxs.UNDEFINED);
    }

    /**
     * Constructor.
     * @param stack Stack size.
     * @param locals Locals size.
     */
    public BytecodeMaxs(final int stack, final int locals) {
        this.stack = stack;
        this.locals = locals;
    }

    /**
     * Stack size.
     * @return Stack size.
     */
    public int stack() {
        return this.stack;
    }

    /**
     * Locals size.
     * @return Locals size.
     */
    public int locals() {
        return this.locals;
    }

    /**
     * Convert to directives.
     * @param format Format of the directives.
     * @return Directives.
     */
    public DirectivesMaxs directives(final Format format) {
        return new DirectivesMaxs(format, this.stack, this.locals);
    }

    /**
     * Is maxs stack and locals are zero?
     * @return True if both are zero.
     */
    boolean compute() {
        return this.stack == BytecodeMaxs.UNDEFINED && this.locals == BytecodeMaxs.UNDEFINED;
    }
}
