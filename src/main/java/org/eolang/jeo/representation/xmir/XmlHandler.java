/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eolang.jeo.representation.bytecode.BytecodeHandler;

/**
 * XML representation of handler.
 * @since 0.3
 */
final class XmlHandler {

    /**
     * XML node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param xmlnode Node.
     */
    XmlHandler(final XmlNode xmlnode) {
        this.node = xmlnode;
    }

    /**
     * Convert to a handler.
     * @return Handler.
     */
    public BytecodeHandler bytecode() {
        final List<XmlOperand> operands = this.node.children()
            .map(XmlOperand::new)
            .collect(Collectors.toList());
        return new BytecodeHandler(
            (Integer) Objects.requireNonNull(operands.get(0).asObject()),
            Objects.requireNonNull(operands.get(1).asObject()).toString(),
            Objects.requireNonNull(operands.get(2).asObject()).toString(),
            Objects.requireNonNull(operands.get(3).asObject()).toString(),
            (Boolean) Objects.requireNonNull(operands.get(4).asObject())
        );
    }
}
