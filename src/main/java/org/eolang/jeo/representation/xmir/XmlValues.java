/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

/**
 * Xmir representation of values.
 * Mirrors {@link org.eolang.jeo.representation.directives.DirectivesValues}.
 * @since 0.14.0
 */
final class XmlValues {

    /**
     * Xmir node representing values.
     */
    private final XmlNode root;

    /**
     * Constructor.
     * @param root Xmir node representing values
     */
    XmlValues(final XmlNode root) {
        this.root = root;
    }

    /**
     * Parse values from the Xmir node.
     * @return Parsed values as an array of objects
     */
    Object[] values() {
        return new XmlSeq(this.root)
            .children()
            .map(XmlOperand::new)
            .map(XmlOperand::asObject)
            .toArray(Object[]::new);
    }
}
