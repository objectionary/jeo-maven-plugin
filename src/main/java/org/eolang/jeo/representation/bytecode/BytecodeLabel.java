/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.bytecode;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesEoObject;
import org.eolang.jeo.representation.directives.DirectivesValue;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Mark label instruction.
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
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
     * @param uid Identifier.
     */
    public BytecodeLabel(final byte[] uid) {
        this(new String(uid, StandardCharsets.UTF_8));
    }

    /**
     * Constructor.
     * @param label Identifier.
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
    public Iterable<Directive> directives(final boolean counting) {
        final Iterable<Directive> result;
        if (Objects.isNull(this.identifier)) {
            result = new DirectivesEoObject("nop");
        } else {
            result = new DirectivesValue(this);
        }
        return result;
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

    public String uid() {
        return this.identifier;
    }
}
