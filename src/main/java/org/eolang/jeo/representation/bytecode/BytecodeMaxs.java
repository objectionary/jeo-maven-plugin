/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesMaxs;

/**
 * Bytecode maxs.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public final class BytecodeMaxs {

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
        this(0, 0);
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
     * @return Directives.
     */
    public DirectivesMaxs directives() {
        return new DirectivesMaxs(this.stack, this.locals);
    }

    /**
     * Is maxs stack and locals are zero?
     * @return True if both are zero.
     */
    boolean compute() {
        return this.stack == 0 && this.locals == 0;
    }
}
