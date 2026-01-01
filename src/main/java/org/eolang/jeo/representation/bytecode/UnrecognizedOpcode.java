/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

/**
 * Unrecognized opcode.
 * @since 0.1
 */
final class UnrecognizedOpcode extends IllegalStateException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 28L;

    /**
     * Constructor.
     * @param opcode Opcode.
     */
    UnrecognizedOpcode(final int opcode) {
        super(String.format("Unrecognized opcode: %d", opcode));
    }
}
