/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesLine;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode line.
 * This class represents the reference to a source line from a bytecode instruction.
 *
 * @since 0.6
 */
public final class BytecodeLine implements BytecodeEntry {

    /**
     * Line number in the source code.
     */
    private final int number;

    /**
     * Bytecode label that this line refers to.
     */
    private final BytecodeLabel label;

    /**
     * Constructor.
     *
     * @param number Line number in the source code
     * @param label Bytecode label that this line refers to
     */
    public BytecodeLine(final int number, final BytecodeLabel label) {
        this.number = number;
        this.label = label;
    }

    @Override
    public void writeTo(final MethodVisitor visitor, final AsmLabels labels) {
        visitor.visitLineNumber(this.number, labels.label(this.label));
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        return new DirectivesLine(index, format, this.number, this.label.uid());
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

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeLine) {
            final BytecodeLine line = (BytecodeLine) other;
            result = this.number == line.number && Objects.equals(this.label, line.label);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.number, this.label);
    }

    @Override
    public String toString() {
        return String.format("BytecodeLine(number=%d, label=%s)", this.number, this.label);
    }
}
