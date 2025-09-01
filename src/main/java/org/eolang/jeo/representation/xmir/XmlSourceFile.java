/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAttribute;

/**
 * Convert XML SourceFile node to bytecode attribute.
 * @since 0.14.0
 */
public final class XmlSourceFile {

    /**
     * JEO XML node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node JEO XML node.
     */
    public XmlSourceFile(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Retrieve source file attributes.
     * @return SourceFile attribute.
     */
    public BytecodeAttribute attribute() {
        return new BytecodeAttribute.SourceFile(
            this.source(),
            this.debug()
        );
    }

    /**
     * Path to the source file.
     * @return Path to the source file.
     */
    private String source() {
        return new XmlValue(
            this.node.child(0).orElseThrow(
                () -> new IllegalStateException("Source file node is absent")
            )
        ).string();
    }

    /**
     * Path to the debug information.
     * @return Path to the debug information.
     */
    private String debug() {
        return new XmlValue(
            this.node.child(1).orElseThrow(
                () -> new IllegalStateException("Source file node is absent")
            )
        ).string();
    }
}
