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
     * Pretty printing option.
     * <p>Indicates whether the output should be formatted for readability.</p>
     */
    private final boolean pretty;

    /**
     * Constructor with default parameters.
     * <p>Uses DEBUG mode, listings disabled, and pretty-printing enabled.</p>
     */
    public DisassembleParams() {
        this(DisassembleMode.SHORT, false, true);
    }

    /**
     * Constructor.
     *
     * @param mode Disassemble mode
     * @param listings Whether to include listings in the output
     * @param pretty Whether to pretty-print the output
     */
    public DisassembleParams(
        final DisassembleMode mode,
        final boolean listings,
        final boolean pretty
    ) {
        this.mode = mode;
        this.listings = listings;
        this.pretty = pretty;
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

    /**
     * Whether to pretty-print the output.
     * @return True if pretty-printing is enabled, false otherwise
     */
    public boolean prettyPrint() {
        return this.pretty;
    }
}
