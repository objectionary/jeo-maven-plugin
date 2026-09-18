/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeAttribute;

/**
 * Convert XML SourceFile node to bytecode attribute.
 *
 * @since 0.14.0
 */
public final class XmlSourceFile {

    /**
     * JEO XML node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     *
     * @param node JEO XML node
     */
    public XmlSourceFile(final XmlJeoObject node) {
        this.node = node;
    }

    /**
     * Retrieve source file attributes.
     *
     * @return SourceFile attribute
     */
    public BytecodeAttribute attribute() {
        return new BytecodeAttribute.SourceFile(
            this.source(),
            this.debug()
        );
    }

    private String source() {
        return this.string(0, "name");
    }

    private String debug() {
        return this.string(1, "debug extension");
    }

    private String string(final int index, final String member) {
        return new XmlValue(
            this.node.child(index).orElseThrow(
                () -> new IllegalStateException(
                    String.format("Source file %s is missing in '%s'", member, this.node)
                )
            )
        ).string();
    }
}
