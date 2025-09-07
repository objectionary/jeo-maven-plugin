/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Locale;
import org.objectweb.asm.ClassReader;

/**
 * Enumeration of bytecode disassembly modes.
 *
 * <p>This enumeration defines different modes for disassembling Java bytecode,
 * controlling the level of detail and information included in the disassembly process.</p>
 * @since 0.6.0
 */
public enum DisassembleMode {
    /**
     * Short mode - disassemble bytecode without debug information.
     */
    SHORT,
    /**
     * Debug mode - disassemble bytecode with full debug information.
     */
    DEBUG;

    /**
     * Unknown mode message.
     */
    private static final String UNKNOWN = "Unknown disassemble mode: %s";

    /**
     * Convert string representation to DisassembleMode.
     * @param mode The string representation of the mode
     * @return The corresponding DisassembleMode
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static DisassembleMode fromString(final String mode) {
        final DisassembleMode result;
        switch (mode.toLowerCase(Locale.ROOT)) {
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
     * Convert to corresponding ASM ClassReader options.
     * @return The ASM ClassReader options for this mode
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
