/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAttribute;

/**
 * Xmir representation of EnclosingMethod attribute.
 *
 * @since 0.14.0
 */
final class XmlEnclosingMethod {

    /**
     * JEO XML node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param node JEO XML node
     */
    XmlEnclosingMethod(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse bytecode attribute.
     *
     * @return Bytecode attribute
     */
    BytecodeAttribute attribute() {
        return new BytecodeAttribute.EnclosingMethod(
            this.owner(),
            this.method(),
            this.descriptor()
        );
    }

    private String owner() {
        return this.string(0);
    }

    private String method() {
        return this.string(1);
    }

    private String descriptor() {
        return this.string(2);
    }

    private String string(final int index) {
        return new XmlValue(
            this.node.child(index).orElseThrow(
                () -> new IllegalStateException("Enclosing method owner is absent")
            )
        ).string();
    }
}
