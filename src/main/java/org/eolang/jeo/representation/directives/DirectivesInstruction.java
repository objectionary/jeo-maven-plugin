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
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Instruction directives.
 * Parses bytecode instruction and transforms it into Xembly directives that further
 * will be converted into XML.
 * @since 0.1
 */
public final class DirectivesInstruction implements Iterable<Directive> {

    /**
     * Opcode.
     */
    private final int opcode;

    /**
     * Instruction arguments.
     */
    private final Object[] arguments;

    /**
     * Constructor.
     * @param opcode Opcode
     * @param arguments Instruction arguments
     */
    public DirectivesInstruction(final int opcode, final Object... arguments) {
        this.opcode = opcode;
        this.arguments = arguments.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives();
        directives.add("o")
            .attr("name", new OpcodeName(this.opcode).asString())
            .attr("base", "opcode");
        directives.append(new DirectivesOperand(this.opcode));
        for (final Object operand : this.arguments) {
            directives.append(new DirectivesOperand(operand));
        }
        directives.up();
        return directives.iterator();
    }
}
