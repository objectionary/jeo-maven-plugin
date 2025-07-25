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
     * Whether to omit comments in the output.
     * <p>When true, comments will not be included in the disassembled output.</p>
     */
    private final boolean comments;

    /**
     * Constructor with default parameters.
     * <p>Uses DEBUG mode, listings disabled, and pretty-printing enabled.</p>
     */
    public DisassembleParams() {
        this(DisassembleMode.SHORT, false, true, false);
    }

    /**
     * Constructor.
     *
     * @param mode Disassemble mode
     * @param listings Whether to include listings in the output
     * @param pretty Whether to pretty-print the output
     * @param comments Whether to include comments in the output
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public DisassembleParams(
        final DisassembleMode mode,
        final boolean listings,
        final boolean pretty,
        final boolean comments
    ) {
        this.mode = mode;
        this.listings = listings;
        this.pretty = pretty;
        this.comments = comments;
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

    /**
     * Whether to include comments in the output.
     * @return True if comments should be included, false otherwise
     */
    public boolean includeComments() {
        return this.comments;
    }
}
