/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

/**
 * Frame directives.
 * <p>All the directives of the frame are sorted according to the JVM specification:
 * {@code
 * full_frame {
 *     u1 frame_type = FULL_FRAME;
 *     u2 offset_delta;
 *     u2 number_of_locals;
 *     verification_type_info locals[number_of_locals];
 *     u2 number_of_stack_items;
 *     verification_type_info stack[number_of_stack_items];
 * }}
 * </p>
 * @since 0.3
 */
public final class DirectivesFrame implements Iterable<Directive> {

    /**
     * Index of the bytecode instruction among other instructions.
     */
    private final int index;

    /**
     * Format of the directives.
     */
    private final Format format;

    /**
     * The type of stack map frame.
     */
    private final int type;

    /**
     * The local variable types in this frame.
     * Primitive types are represented by:
     * Opcodes.TOP, Opcodes.INTEGER, Opcodes.FLOAT, Opcodes.LONG, Opcodes.DOUBLE, Opcodes.NULL
     * or Opcodes.UNINITIALIZED_THIS.
     * - Reference types are represented by String objects
     * - Uninitialized types by Label objects, e.g. for the NEW instruction that
     * created this uninitialized value.
     */
    private final Object[] locals;

    /**
     * The operand stack types in this frame.
     */
    private final Object[] stack;

    /**
     * Constructor.
     * @param index Index of the bytecode instruction among other instructions.
     * @param format Format of the directives.
     * @param type The type of stack map frame.
     * @param locals The local variable types in this frame.
     * @param stack The operand stack types in this frame.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesFrame(
        final int index,
        final Format format,
        final int type,
        final Object[] locals,
        final Object... stack
    ) {
        this.index = index;
        this.format = format;
        this.type = type;
        this.locals = locals.clone();
        this.stack = stack.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "frame",
            new NumName("f", this.index).toString(),
            new DirectivesValue(this.format, DirectivesFrame.name("type"), this.type),
            new DirectivesFrameValues(this.format, DirectivesFrame.name("locals"), this.locals),
            new DirectivesFrameValues(this.format, DirectivesFrame.name("stack"), this.stack)
        ).iterator();
    }

    /**
     * Attribute name.
     * @param attribute Attribute name.
     * @return Attribute name.
     */
    private static String name(final String attribute) {
        return attribute;
    }
}
