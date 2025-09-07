/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAttribute;

/**
 * Xmir representation of NestHost attribute.
 * @since 0.14.0
 */
final class XmlNestHost {

    /**
     * JEO XML node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node JEO XML node.
     */
    XmlNestHost(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse bytecode attribute.
     * @return Bytecode attribute.
     */
    BytecodeAttribute attribute() {
        return new BytecodeAttribute.NestHost(
            new XmlValue(
                this.node.child(0).orElseThrow(
                    () -> new IllegalArgumentException(
                        String.format("NestHost attribute is malformed: %s", this.node)
                    )
                )
            ).string()
        );
    }
}
