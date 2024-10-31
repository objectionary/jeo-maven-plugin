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

import java.util.List;
import org.objectweb.asm.Label;
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
     */
    void writeTo(MethodVisitor visitor);

    Iterable<Directive> directives(boolean counting);

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
    List<Label> jumps();

    /**
     * Human-readable representation.
     * @return Text.
     */
    String view();
}
