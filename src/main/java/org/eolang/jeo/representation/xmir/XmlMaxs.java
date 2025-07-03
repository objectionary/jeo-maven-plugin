/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.Objects;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;

/**
 * Xmir representation of max stack and max locals of a method.
 * @since 0.3
 */
@EqualsAndHashCode
@ToString
public final class XmlMaxs {

    /**
     * XML node.
     */
    @EqualsAndHashCode.Exclude
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node XML Jeo object node representing the maxs.
     */
    XmlMaxs(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Convert to bytecode maxs.
     * @return Bytecode maxs.
     */
    public BytecodeMaxs bytecode() {
        return new BytecodeMaxs(this.stack(), this.locals());
    }

    /**
     * Stack max size.
     * @return Stack size.
     */
    @EqualsAndHashCode.Include
    private int stack() {
        return this.ichild(0);
    }

    /**
     * Locals max size.
     * @return Locals size.
     */
    @EqualsAndHashCode.Include
    private int locals() {
        return this.ichild(1);
    }

    /**
     * Retrieve integer child.
     * @param position Position.
     * @return Integer value.
     */
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
