/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Objects;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;

/**
 * Xmir representation of max stack and max locals of a method.
 *
 * @since 0.3
 */
public final class XmlMaxs {

    /**
     * XML node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param node XML Jeo object node representing the maxs
     */
    XmlMaxs(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Convert to bytecode maxs.
     *
     * @return Bytecode maxs
     */
    public BytecodeMaxs bytecode() {
        return new BytecodeMaxs(this.stack(), this.locals());
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof XmlMaxs) {
            final XmlMaxs maxs = (XmlMaxs) other;
            result = this.stack() == maxs.stack() && this.locals() == maxs.locals();
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.stack(), this.locals());
    }

    @Override
    public String toString() {
        return String.format("XmlMaxs(node=%s)", this.node);
    }

    private int stack() {
        return this.ichild(0);
    }

    private int locals() {
        return this.ichild(1);
    }

    private int ichild(final int position) {
        return (int) Objects.requireNonNull(
            new XmlOperand(
                this.node.children().collect(Collectors.toList()).get(position)
            ).asObject(),
            String.format(
                "The XML node representing Maxs '%s' doesn't contain a valid integer at '%d' position",
                this.node,
                position
            )
        );
    }
}
