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

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
     * Opcodes counting.
     * Do we add number to opcode name or not?
     * if true, then we add number to opcode name:
     *   RETURN -> RETURN-1
     * if false, then we do not add number to opcode name:
     *   RETURN -> RETURN
     */
    private final boolean counting;

    /**
     * Constructor.
     * @param opcode Opcode
     * @param counting Opcodes counting
     * @param arguments Instruction arguments
     */
    public DirectivesInstruction(
        final int opcode,
        final boolean counting,
        final Object... arguments
    ) {
        this.opcode = opcode;
        this.counting = counting;
        this.arguments = arguments.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "opcode",
            this.name(),
            Stream.concat(
                Stream.of(new DirectivesOperand(this.opcode)),
                Arrays.stream(this.arguments).map(DirectivesOperand::new)
            ).map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }

    /**
     * Get opcode name.
     * @return Opcode name.
     */
    private String name() {
        final String result;
        if (this.counting) {
            result = new OpcodeName(this.opcode).asString();
        } else {
            result = new OpcodeName(this.opcode).simplified();
        }
        return result;
    }
}
