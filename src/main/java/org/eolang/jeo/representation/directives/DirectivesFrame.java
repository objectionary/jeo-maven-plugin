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
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

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
            new DirectivesValue(this.type),
            new DirectivesValue(this.nlocal),
            new DirectivesValues(this.locals),
            new DirectivesValue(this.nstack),
            new DirectivesValues(this.stack)
        ).iterator();
//        return new Directives()
//            .add("o")
//            .attr("base", new JeoFqn("frame").fqn())
//            .append(new DirectivesValue(this.type))
//            .append(new DirectivesValue(this.nlocal))
//            .append(new DirectivesValues(this.locals))
//            .append(new DirectivesValue(this.nstack))
//            .append(new DirectivesValues(this.stack))
//            .up()
//            .iterator();
    }
}
