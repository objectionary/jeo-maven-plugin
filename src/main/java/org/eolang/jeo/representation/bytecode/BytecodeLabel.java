/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesLabel;
import org.eolang.jeo.representation.xmir.AllLabels;
import org.objectweb.asm.Label;
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
     * Label.
     */
    private final Label label;

    /**
     * All method labels.
     */
    private final AllLabels labels;

    /**
     * Constructor.
     * @param identifier Label identifier.
     */
    public BytecodeLabel(final String identifier) {
        this(identifier, new AllLabels());
    }

    /**
     * Constructor.
     * @param identifier Label identifier.
     * @param labels All labels.
     */
    public BytecodeLabel(final String identifier, final AllLabels labels) {
        this(labels.label(identifier), labels);
    }

    /**
     * Constructor.
     * @param label Label.
     * @param labels All labels.
     */
    public BytecodeLabel(final Label label, final AllLabels labels) {
        this.label = label;
        this.labels = labels;
    }

    @Override
    public void writeTo(final MethodVisitor visitor) {
        visitor.visitLabel(this.label);
    }

    @Override
    public Iterable<Directive> directives(final boolean counting) {
        return new DirectivesLabel(this.label);
    }

    @Override
    public boolean isLabel() {
        return true;
    }

    @Override
    public boolean isOpcode() {
        return false;
    }

    @Override
    public String testCode() {
        return String.format(".label(\"%s\")", this.labels.uid(this.label));
    }
}
