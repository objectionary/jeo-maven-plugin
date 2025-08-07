/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

/**
 * Frame directives.
 *
 * @since 0.3
 */
public final class DirectivesFrame implements Iterable<Directive> {

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
     * @param type The type of stack map frame.
     * @param locals The local variable types in this frame.
     * @param stack The operand stack types in this frame.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesFrame(
        final int type,
        final Object[] locals,
        final Object... stack
    ) {
        this.type = type;
        this.locals = locals.clone();
        this.stack = stack.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "frame",
            new RandName("f").toString(),
            new DirectivesValue(this.name("type"), this.type),
            new DirectivesFrameValues(this.name("locals"), this.locals),
            new DirectivesFrameValues(this.name("stack"), this.stack)
        ).iterator();
    }

    /**
     * Attribute name.
     * @param attribute Attribute name.
     * @return Attribute name.
     */
    private String name(final String attribute) {
        return String.format("%s-%d", attribute, this.hashCode());
    }
}
