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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.asm.AsmLabels;
import org.eolang.jeo.representation.directives.DirectivesFrame;
import org.eolang.jeo.representation.directives.Format;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.FrameNode;
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
        new FrameNode(
            this.type,
            this.nlocal,
            this.asmLocals(labels),
            this.nstack,
            this.asmStack(labels)
        ).accept(visitor);
    }

    @Override
    public Iterable<Directive> directives(final int index, final Format format) {
        return new DirectivesFrame(
            index,
            format,
            this.type,
            this.locals,
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
        return String.format(
            "Frame %d locals %d stack %d",
            this.type,
            this.nlocal,
            this.nstack
        );
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
