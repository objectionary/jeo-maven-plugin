/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

/**
 * Parameters for disassembling bytecode.
 * @since 0.11.0
 */
public final class DisassembleParams {

    /**
     * Disassemble mode.
     */
    private final DisassembleMode mode;

    /**
     * Whether to include listings in the output.
     */
    private final boolean listings;

    /**
     * Constructor with default parameters.
     * <p>Uses SHORT mode and listings disabled.</p>
     */
    public DisassembleParams() {
        this(DisassembleMode.DEBUG, false);
    }

    /**
     * Constructor.
     * @param mode Disassemble mode
     * @param listings Whether to include listings in the output
     */
    public DisassembleParams(final DisassembleMode mode, final boolean listings) {
        this.mode = mode;
        this.listings = listings;
    }

    /**
     * Disassemble mode.
     * @return Disassemble mode as ASM ClassReader options
     */
    public int asmMode() {
        return this.mode.asmOptions();
    }

    /**
     * Whether to include listings in the output.
     * @return True if listings should be included, false otherwise
     */
    public boolean includeListings() {
        return this.listings;
    }
}
