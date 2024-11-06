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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.directives.DirectivesFrame;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LabelNode;
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
     * @param locals Local variables.
     * @param stack Stack elements.
     */
    public BytecodeFrame(final int type, final List<Object> locals, final List<Object> stack) {
        this(
            type,
            Optional.ofNullable(locals).map(List::size).orElse(0),
            BytecodeFrame.toArray(Optional.ofNullable(locals).orElse(new ArrayList<>(0))),
            Optional.ofNullable(stack).map(List::size).orElse(0),
            BytecodeFrame.toArray(Optional.ofNullable(stack).orElse(new ArrayList<>(0)))
        );
    }

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
    public Iterable<Directive> directives(final boolean counting) {
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
    public List<Label> jumps() {
        return Collections.emptyList();
    }

    @Override
    public String view() {
        final String name;
        switch (this.type) {
            case Opcodes.F_NEW:
                name = "NEW";
                break;
            case Opcodes.F_FULL:
                name = "FULL";
                break;
            case Opcodes.F_APPEND:
                name = "APPEND";
                break;
            case Opcodes.F_CHOP:
                name = "CHOP";
                break;
            case Opcodes.F_SAME:
                name = "SAME";
                break;
            case Opcodes.F_SAME1:
                name = "SAME1";
                break;
            default:
                name = "UNKNOWN";
        }
        return String.format(
            "Frame %s locals %d stack %d",
            name,
            this.nlocal,
            this.nstack
        );
    }

    public int stackDiff(final BytecodeFrame other) {
        return this.nstack - other.nstack;
    }


    public boolean stackOneElement() {
        return this.nstack == 1;
    }

    public boolean stackEmpty() {
        return this.nstack == 0;
    }

    public BytecodeFrame withType(final int type) {
        return new BytecodeFrame(
            type,
            this.nlocal,
            this.locals,
            this.nstack,
            this.stack
        );
    }

    public BytecodeFrame substract(final BytecodeFrame other) {
        final int length = this.nlocal - other.nlocal;
        final Object[] locals;
        if (length <= 0) {
            locals = new Object[0];
        } else {
            locals = new Object[length];
            System.arraycopy(this.locals, length - 1, locals, 0, length);
        }
        final int stackLength = this.nstack - other.nstack;
        final Object[] stack = new Object[stackLength];
        System.arraycopy(this.stack, 0, stack, 0, stackLength);
        return new BytecodeFrame(
            this.type,
            length,
            locals,
            stackLength,
            stack
        );
    }

    public int localsDiff(final BytecodeFrame previous) {
        return this.nlocal - previous.nlocal;
    }

    /**
     * Check if the frame has the same locals.
     * @param frame Frame to compare.
     * @return True if the frame has the same locals.
     */
    public boolean sameLocals(final BytecodeFrame frame) {
        final boolean size = this.nlocal == frame.nlocal;
        final Object[] current = this.locals;
        final Object[] other = frame.locals;
        for (int index = 0; index < this.nlocal; ++index) {
            if (!current[index].equals(other[index])) {
                return false;
            }
        }
        return size;
    }

    /**
     * Convert a list to array.
     * @param list List of objects.
     * @return Array of objects.
     */
    private static Object[] toArray(final List<Object> list) {
        return list.stream().map(BytecodeFrame::extract).toArray(Object[]::new);
    }

    /**
     * Extract an object.
     * @param argument Argument.
     * @return Object.
     */
    private static Object extract(final Object argument) {
        final Object result;
        if (argument instanceof LabelNode) {
            result = ((LabelNode) argument).getLabel();
        } else {
            result = argument;
        }
        return result;
    }
}
