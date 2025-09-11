/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeUnknownAttribute;

/**
 * XML unknown attribute.
 * Mirror of {@link org.eolang.jeo.representation.bytecode.BytecodeUnknownAttribute}
 * @since 0.15.0
 */
public final class XmlUnknownAttribute {

    /**
     * Unknown attribute node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node Unknown attribute node.
     */
    public XmlUnknownAttribute(final XmlNode node) {
        this(new XmlJeoObject(node));
    }

    /**
     * Constructor.
     * @param node Unknown attribute node.
     */
    public XmlUnknownAttribute(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Parse bytecode unknown attribute from XML.
     * @return Bytecode of the unknown attribute.
     */
    public BytecodeUnknownAttribute bytecode() {
        return new BytecodeUnknownAttribute(
            this.type(),
            this.data()
        );
    }

    /**
     * Parse type from XML.
     * @return Type of the attribute.
     */
    private String type() {
        return new XmlValue(
            this.node.child(0).orElseThrow(
                () -> new IllegalStateException(
                    String.format("Attribute 'type' is missing in `%s`", this.node)
                )
            )
        ).string();
    }

    /**
     * Parse data from XML.
     * @return Data of the attribute.
     */
    private byte[] data() {
        return (byte[]) new XmlValue(
            this.node.child(1).orElseThrow(
                () -> new IllegalStateException(
                    String.format("Attribute 'data' is missing in `%s`", this.node)
                )
            )
        ).object();
    }
}
