/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.log.Logger;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesTryCatch;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode try-catch block.
 *
 * @since 0.1
 */
public final class BytecodeTryCatchBlock implements BytecodeEntry {

    /**
     * Start label.
     */
    private final BytecodeLabel start;

    /**
     * End label.
     */
    private final BytecodeLabel end;

    /**
     * Handler label.
     */
    private final BytecodeLabel handler;

    /**
     * Exception type.
     */
    private final String type;

    /**
     * Constructor.
     *
     * @param start Start label
     * @param end End label
     * @param handler Handler label
     * @param type Exception type
     */
    public BytecodeTryCatchBlock(
        final String start,
        final String end,
        final String handler,
        final String type
    ) {
        this(new BytecodeLabel(start), new BytecodeLabel(end), new BytecodeLabel(handler), type);
    }

    /**
     * Constructor.
     *
     * @param startlabel Start label
     * @param endlabel End label
     * @param handlerlabel Handler label
     * @param exception Exception type
     */
    public BytecodeTryCatchBlock(
        final BytecodeLabel startlabel,
        final BytecodeLabel endlabel,
        final BytecodeLabel handlerlabel,
        final String exception
    ) {
        this.start = startlabel;
        this.end = endlabel;
        this.handler = handlerlabel;
        this.type = exception;
    }

    @Override
    public void writeTo(final MethodVisitor visitor, final AsmLabels labels) {
        Logger.debug(
            this,
            "Writing try-catch entry into the method with the following values: start=%s, end=%s, handler=%s, type=%s",
            this.start,
            this.end,
            this.handler,
            this.type
        );
        visitor.visitTryCatchBlock(
            labels.label(this.start),
            labels.label(this.end),
            labels.label(this.handler),
            this.type
        );
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        return new DirectivesTryCatch(index, format, this.start, this.end, this.handler, this.type);
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
        return Collections.singletonList(this.handler);
    }

    @Override
    public String view() {
        return String.format(
            "try-catch %s %s %s %s",
            this.start,
            this.end,
            this.handler,
            this.type
        );
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeTryCatchBlock) {
            final BytecodeTryCatchBlock block = (BytecodeTryCatchBlock) other;
            result = Objects.equals(this.start, block.start)
                && Objects.equals(this.end, block.end)
                && Objects.equals(this.handler, block.handler)
                && Objects.equals(this.type, block.type);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.start, this.end, this.handler, this.type);
    }

    @Override
    public String toString() {
        return String.format(
            "BytecodeTryCatchBlock(start=%s, end=%s, handler=%s, type=%s)",
            this.start, this.end, this.handler, this.type
        );
    }

    /**
     * Start label.
     *
     * @return Label
     */
    BytecodeLabel startLabel() {
        return this.start;
    }

    /**
     * End label.
     *
     * @return Label
     */
    BytecodeLabel endLabel() {
        return this.end;
    }

    /**
     * Handler label.
     *
     * @return Label
     */
    BytecodeLabel handlerLabel() {
        return this.handler;
    }
}
