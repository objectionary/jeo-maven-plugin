/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Bytecode method builder.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodeMethodBuilder {

    /**
     * Class for the method.
     */
    private final BytecodeClass clazz;

    /**
     * Bytecode method.
     */
    private final BytecodeMethod method;

    /**
     * Constructor.
     * @param clazz Class.
     * @param method Method.
     */
    public BytecodeMethodBuilder(final BytecodeClass clazz, final BytecodeMethod method) {
        this.clazz = clazz;
        this.method = method;
    }

    /**
     * Return to the original class.
     * @return Original class.
     * @checkstyle MethodNameCheck (3 lines)
     */
    @SuppressWarnings("PMD.ShortMethodName")
    public BytecodeClass up() {
        return this.clazz;
    }

    public BytecodeMethodBuilder label() {
        return this.label(UUID.randomUUID().toString());
    }

    /**
     * Add label.
     * @param uid Label uid.
     * @return This object.
     */
    public BytecodeMethodBuilder label(final String uid) {
        this.method.label(uid);
        return this;
    }

    /**
     * Add instruction.
     * @param opcode Opcode.
     * @param args Arguments.
     * @return This object.
     */
    public BytecodeMethodBuilder opcode(final int opcode, final Object... args) {
        this.method.opcode(opcode, args);
        return this;
    }

    /**
     * Add try-catch block.
     * @param entry Try-catch block.
     * @return This object.
     */
    public BytecodeMethodBuilder trycatch(final BytecodeEntry entry) {
        this.method.trycatch(entry);
        return this;
    }

}
