/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import java.nio.charset.StandardCharsets;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;

/**
 * XML representation of bytecode label.
 * @since 0.1
 */
public final class XmlLabel implements XmlBytecodeEntry {

    /**
     * Label node.
     */
    private final XmlJeoObject node;

    /**
     * Constructor.
     * @param node Label node.
     */
    XmlLabel(final XmlNode node) {
        this.node = new XmlJeoObject(node);
    }

    /**
     * Converts label to bytecode.
     * @return Bytecode label.
     */
    public BytecodeLabel bytecode() {
        return new BytecodeLabel(
            new String(
                (byte[]) new XmlValue(
                    this.node.children()
                        .findFirst()
                        .orElseThrow(
                            () -> new IllegalStateException(
                                "Label node must have at least one child"
                            )
                        )
                ).object(),
                StandardCharsets.UTF_8
            )
        );
    }
}
