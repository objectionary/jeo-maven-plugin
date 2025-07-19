/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesFrame;
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
    public void writeTo(final MethodVisitor visitor, final AsmLabels labels) {
        visitor.visitFrame(
            this.type,
            this.nlocal,
            this.asmLocals(labels),
            this.nstack,
            this.asmStack(labels)
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
        List<Object> result = new ArrayList<>(0);
        final Object[] tlocals = this.locals;
        final Object[] olocals = other.locals;
        int size = Math.max(tlocals.length, olocals.length);
        for (int i = 0; i < size; ++i) {
            final Object tlocal;
            if (tlocals.length <= i) {
                tlocal = Opcodes.TOP;
            } else {
                tlocal = tlocals[i];
            }
            final Object olocal;
            if (olocals.length <= i) {
                olocal = Opcodes.TOP;
            } else {
                olocal = olocals[i];
            }
            if (!tlocal.equals(olocal)) {
                result.add(tlocal);
            }
        }
        Object[] res = result.stream().filter(arg -> !arg.equals(Opcodes.TOP))
            .toArray();
        return new BytecodeFrame(
            this.type,
            res.length,
            res,
            this.nstack,
            this.stack
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
        final int size = Math.max(this.nlocal, frame.nlocal);
        final Object[] current = this.locals;
        final Object[] other = frame.locals;
        for (int index = 0; index < size; ++index) {
            final Object curr;
            if (current.length <= index) {
                curr = Opcodes.TOP;
            } else {
                curr = current[index];
            }
            final Object oth;
            if (other.length <= index) {
                oth = Opcodes.TOP;
            } else {
                oth = other[index];
            }
            if (!curr.equals(oth)) {
                return false;
            }
        }
        return true;
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
     * Convert stack to ASM format.
     * @param labels Method labels.
     * @return Stack in ASM format.
     */
    private Object[] asmStack(final AsmLabels labels) {
        return BytecodeFrame.asmOperands(this.stack, labels);
    }

    /**
     * Convert locals to ASM format.
     * @param labels Method labels.
     * @return Locals in ASM format.
     */
    private Object[] asmLocals(final AsmLabels labels) {
        return BytecodeFrame.asmOperands(this.locals, labels);
    }

    /**
     * Convert operands to ASM format.
     * @param arr Operands.
     * @param labels Method labels.
     * @return Operands in ASM format.
     */
    private static Object[] asmOperands(final Object[] arr, final AsmLabels labels) {
        return Arrays.stream(arr).map(
            obj -> {
                final Object result;
                if (obj instanceof BytecodeLabel) {
                    result = labels.label((BytecodeLabel) obj);
                } else {
                    result = obj;
                }
                return result;
            }
        ).toArray();
    }

    /**
     * Extract an object.
     * @param argument Argument.
     * @return Object.
     */
    private static Object extract(final Object argument) {
        final Object result;
        if (argument instanceof LabelNode) {
            result = new BytecodeLabel(((LabelNode) argument).getLabel().toString());
        } else {
            result = argument;
        }
        return result;
    }
}
