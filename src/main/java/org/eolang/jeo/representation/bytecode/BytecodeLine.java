/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode line.
 * This class represents the reference to a source line from a bytecode instruction.
 * Since the purpose of this class is rather informative, we just ignore it.
 * @since 0.6
 */
@EqualsAndHashCode
@ToString
public final class BytecodeLine implements BytecodeEntry {

    @Override
    public void writeTo(final MethodVisitor visitor, final AsmLabels labels) {
        // nothing to write
    }

    @Override
    public Iterable<Directive> directives() {
        return Collections.emptyList();
    }

    @Override
    public boolean isLabel() {
        return false;
    }

    @Override
    public boolean isSwitch() {
        return false;
    }

    @Override
    public boolean isJump() {
        return false;
    }

    @Override
    public boolean isIf() {
        return false;
    }

    @Override
    public boolean isReturn() {
        return false;
    }

    @Override
    public boolean isThrow() {
        return false;
    }

    @Override
    public boolean isOpcode() {
        return false;
    }

    @Override
    public int impact() {
        return 0;
    }

    @Override
    public List<BytecodeLabel> jumps() {
        return Collections.emptyList();
    }

    @Override
    public String view() {
        return "line";
    }
}
