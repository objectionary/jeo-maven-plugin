/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import org.objectweb.asm.ClassReader;

/**
 * Disassemble mode.
 * @since 0.6
 */
public enum DisassembleMode {
    /**
     * Short mode.
     * This mode will disassemble the bytecode without any additional information.
     */
    SHORT,
    /**
     * Assemble mode.
     */
    DEBUG;

    /**
     * Unknown mode message.
     */
    private static final String UNKNOWN = "Unknown disassemble mode: %s";

    /**
     * Convert from string.
     * @param mode Mode.
     * @return Disassemble mode.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static DisassembleMode fromString(final String mode) {
        final DisassembleMode result;
        switch (mode) {
            case "short":
                result = DisassembleMode.SHORT;
                break;
            case "debug":
                result = DisassembleMode.DEBUG;
                break;
            default:
                throw new IllegalArgumentException(String.format(DisassembleMode.UNKNOWN, mode));
        }
        return result;
    }

    /**
     * Convert to ASM options.
     * @return ASM options.
     */
    public int asmOptions() {
        final int result;
        switch (this) {
            case SHORT:
                result = ClassReader.SKIP_DEBUG;
                break;
            case DEBUG:
                result = 0;
                break;
            default:
                throw new IllegalArgumentException(String.format(DisassembleMode.UNKNOWN, this));
        }
        return result;
    }
}
