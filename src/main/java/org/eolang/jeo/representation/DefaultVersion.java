/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.objectweb.asm.Opcodes;

/**
 * Version of ASM and Java bytecode.
 * @since 0.1.0
 */
public final class DefaultVersion {

    /**
     * Java bytecode version.
     */
    private final int bcode;

    /**
     * ASM API version.
     */
    private final int asm;

    /**
     * Default constructor.
     */
    public DefaultVersion() {
        this(Opcodes.V1_8, Opcodes.ASM9);
    }

    /**
     * Constructor.
     * @param bytecode Java bytecode version.
     * @param api ASM API version.
     */
    private DefaultVersion(final int bytecode, final int api) {
        this.bcode = bytecode;
        this.asm = api;
    }

    /**
     * Java bytecode version.
     * @return Java bytecode version.
     */
    public int bytecode() {
        return this.bcode;
    }

    /**
     * ASM API version.
     * @return ASM API version.
     */
    public int api() {
        return this.asm;
    }
}
