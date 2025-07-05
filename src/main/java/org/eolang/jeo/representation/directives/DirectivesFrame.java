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
     * The number of local variables in the visited frame.
     * Long and double values count for one variable.
     */
    private final int nlocal;

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
     * The number of operand stack elements in the visited frame.
     */
    private final int nstack;

    /**
     * The operand stack types in this frame.
     */
    private final Object[] stack;

    /**
     * Constructor.
     * @param type The type of stack map frame.
     * @param nlocal The number of local variables in the visited frame.
     * @param locals The local variable types in this frame.
     * @param nstack The number of operand stack elements in the visited frame.
     * @param stack The operand stack types in this frame.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesFrame(
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
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "frame",
            new RandName("frame").toString(),
            new DirectivesValue(this.name("type"), this.type),
            new DirectivesValue(this.name("nlocal"), this.nlocal),
            new DirectivesValues(this.name("locals"), this.locals),
            new DirectivesValue(this.name("nstack"), this.nstack),
            new DirectivesValues(this.name("stack"), this.stack)
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
