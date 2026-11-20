/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Objects;

/**
 * Scope of a type annotation that targets a local variable.
 * A local variable annotation may cover several ranges of the variable
 * at once, ASM models them with parallel start/end/index lists. Each
 * range is represented by one instance of this class.
 *
 * @since 0.15.0
 */
public final class AnnotationScope {

    /**
     * Start label of the scope.
     * It is null when the start is borrowed from the context:
     * the enclosing local variable entry or the label that opens the scope.
     */
    private final BytecodeLabel start;

    /**
     * End label of the scope.
     */
    private final BytecodeLabel end;

    /**
     * Slot index of the local variable in the local variable array.
     */
    private final int slot;

    /**
     * Constructor.
     *
     * @param start Start label of the scope, nullable
     * @param end End label of the scope
     * @param slot Slot index of the local variable
     */
    public AnnotationScope(final BytecodeLabel start, final BytecodeLabel end, final int slot) {
        this.start = start;
        this.end = end;
        this.slot = slot;
    }

    /**
     * Start label of the scope.
     *
     * @return Start label, or null if it is borrowed from the context
     */
    public BytecodeLabel start() {
        return this.start;
    }

    /**
     * End label of the scope.
     *
     * @return End label
     */
    public BytecodeLabel end() {
        return this.end;
    }

    /**
     * Slot index of the local variable.
     *
     * @return Slot index
     */
    public int slot() {
        return this.slot;
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof AnnotationScope) {
            final AnnotationScope scope = (AnnotationScope) other;
            result = this.slot == scope.slot
                && Objects.equals(this.start, scope.start)
                && Objects.equals(this.end, scope.end);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.start, this.end, this.slot);
    }

    @Override
    public String toString() {
        return String.format(
            "AnnotationScope(start=%s, end=%s, slot=%s)",
            this.start, this.end, this.slot
        );
    }
}
