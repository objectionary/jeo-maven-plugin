/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesLabel;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Mark label instruction.
 *
 * @since 0.1
 */
public final class BytecodeLabel implements BytecodeEntry {

    /**
     * Simple string identifier.
     */
    private final String identifier;

    /**
     * Constructor.
     */
    public BytecodeLabel() {
        this(UUID.randomUUID().toString());
    }

    /**
     * Constructor.
     *
     * @param uid Identifier
     */
    public BytecodeLabel(final byte[] uid) {
        this(new String(uid, StandardCharsets.UTF_8));
    }

    /**
     * Constructor.
     *
     * @param label Identifier
     */
    public BytecodeLabel(final String label) {
        this.identifier = label;
    }

    @Override
    public void writeTo(final MethodVisitor visitor, final AsmLabels labels) {
        if (Objects.nonNull(this.identifier)) {
            visitor.visitLabel(labels.label(this));
        }
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        return new DirectivesLabel(index, format, this.identifier);
    }

    @Override
    public boolean isLabel() {
        return true;
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
        return String.format("label %s", this.identifier);
    }

    /**
     * Unique identifier of the label.
     *
     * @return Identifier
     */
    public String uid() {
        return this.identifier;
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other instanceof BytecodeLabel) {
            result = Objects.equals(this.identifier, ((BytecodeLabel) other).identifier);
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.identifier);
    }

    @Override
    public String toString() {
        return String.format("BytecodeLabel(identifier=%s)", this.identifier);
    }
}
