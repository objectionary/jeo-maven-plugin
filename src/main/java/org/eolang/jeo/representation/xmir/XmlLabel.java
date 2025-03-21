/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.xmir;

import org.eolang.jeo.representation.bytecode.BytecodeLabel;

/**
 * XML representation of bytecode label.
 * @since 0.1
 */
public final class XmlLabel implements XmlBytecodeEntry {

    /**
     * Label node.
     */
    private final XmlNode node;

    /**
     * Constructor.
     * @param node Label node.
     */
    XmlLabel(final XmlNode node) {
        this.node = node;
    }

    /**
     * Converts label to bytecode.
     * @return Bytecode label.
     */
    public BytecodeLabel bytecode() {
        return (BytecodeLabel) new XmlValue(this.node).object();
    }
}
