/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.objectweb.asm.Type;

/**
 * ASM type representation in XMIR format.
 * @since 0.11.0
 */
final class XmlType {

    /**
     * Raw XML node representing the type.
     */
    private final XmlJeoObject raw;

    /**
     * Constructor.
     * @param raw Raw XML node representing the type.
     */
    XmlType(final XmlNode raw) {
        this(new XmlJeoObject(raw));
    }

    /**
     * Constructor.
     * @param raw Raw XML Jeo object representing the type.
     */
    XmlType(final XmlJeoObject raw) {
        this.raw = raw;
    }

    /**
     * Parse ASM type from the XML node.
     * @return ASM Type object
     */
    public Type type() {
        try {
            return Type.getType(
                new XmlValue(
                    this.raw.child(0).orElseThrow(
                        () -> new IllegalStateException("Cannot find the first child node")
                    )
                ).string()
            );
        } catch (final ClassCastException exception) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid type descriptor of the first child in XML node: %s",
                    this.raw
                ),
                exception
            );
        }
    }
}
