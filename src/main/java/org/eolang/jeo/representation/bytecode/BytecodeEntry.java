/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.List;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode instruction or a label.
 * Might be a label, a jump, a method call, etc.
 * @since 0.1
 */
public interface BytecodeEntry {
    /**
     * Write instruction to the method visitor.
     * @param visitor Method visitor.
     * @param labels Method labels.
     */
    void writeTo(MethodVisitor visitor, AsmLabels labels);

    /**
     * Convert entry to directives.
     * @return Directives.
     */
    Iterable<Directive> directives();

    /**
     * Is this instruction a label?
     * @return True if it is.
     */
    boolean isLabel();

    /**
     * Is this instruction a switch?
     * @return True if it is.
     */
    boolean isSwitch();

    /**
     * Is this instruction a goto?
     * Is it a goto or jsr?
     * @return True if it is.
     */
    boolean isJump();

    /**
     * Is this instruction a conditional branch?
     * @return True if it is.
     */
    boolean isIf();

    /**
     * Is this instruction a return statement?
     * @return True if it is.
     */
    boolean isReturn();

    /**
     * Is this instruction a throw statement?
     * @return True if it is.
     */
    boolean isThrow();

    /**
     * Is this instruction a regular opcode?
     * @return True if it is.
     */
    boolean isOpcode();

    /**
     * Impact of the instruction on the stack.
     * @return Stack impact.
     */
    int impact();

    /**
     * Jump to a label.
     * Where to jump.
     * @return Jumps.
     */
    List<BytecodeLabel> jumps();

    /**
     * Human-readable representation.
     * @return Text.
     */
    String view();
}
