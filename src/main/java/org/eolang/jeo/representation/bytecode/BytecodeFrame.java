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

import java.util.Arrays;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesFrame;
import org.objectweb.asm.MethodVisitor;
import org.xembly.Directive;

/**
 * Bytecode frame.
 * @since 0.6
 */
@ToString
@EqualsAndHashCode
public final class BytecodeFrame implements BytecodeEntry {

    /**
     * Type.
     */
    private final int type;

    /**
     * Number of locals.
     */
    private final int nlocal;

    /**
     * Locals.
     */
    private final Object[] locals;

    /**
     * Stack size.
     */
    private final int nstack;

    /**
     * Stack.
     */
    private final Object[] stack;

    /**
     * Constructor.
     * @param type Frame type.
     * @param nlocal Number of local variables.
     * @param locals Local variables.
     * @param nstack Number of stack elements.
     * @param stack Stack elements.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BytecodeFrame(
        final int type,
        final int nlocal,
        final Object[] locals,
        final int nstack,
        final Object... stack
    ) {
        this.type = type;
        this.nlocal = nlocal;
        this.locals = locals.clone();
        this.nstack = nstack;
        this.stack = stack.clone();
    }

    @Override
    public void writeTo(final MethodVisitor visitor) {
        visitor.visitFrame(
            this.type,
            this.nlocal,
            this.locals,
            this.nstack,
            this.stack
        );
    }

    @Override
    public Iterable<Directive> directives() {
        return new DirectivesFrame(
            this.type,
            this.nlocal,
            this.locals,
            this.nstack,
            this.stack
        );
    }

    @Override
    public boolean isLabel() {
        return false;
    }

    @Override
    public boolean isOpcode() {
        return false;
    }

    @Override
    public String testCode() {
        return String.format(
            ".visitFrame(%d, %d, new Object[]{ %s }, %d, new Object[]{ %s })",
            this.type,
            this.nlocal,
            Arrays.toString(this.locals),
            this.nstack,
            Arrays.toString(this.stack)
        );
    }
}
