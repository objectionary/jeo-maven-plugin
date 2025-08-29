/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
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
     * Instruction index.
     */
    private final int index;

    /**
     * Format of the directives.
     */
    private final Format format;

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
     * @param index Instruction index
     * @param format Format of the directives
     * @param opcode Opcode
     * @param arguments Instruction arguments
     * @checkstyle ParameterNumber (5 lines)
     */
    public DirectivesInstruction(
        final int index,
        final Format format,
        final int opcode,
        final Object... arguments
    ) {
        this.index = index;
        this.format = format;
        this.opcode = opcode;
        this.arguments = arguments.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        final AtomicInteger counter = new AtomicInteger(0);
        return new DirectivesJeoObject(
            this.base(),
            new NumName("i", this.index).toString(),
            Stream.concat(
                Stream.of(new DirectivesComment(this.format, this.comment())),
                Arrays.stream(this.arguments)
                    .map(a -> new DirectivesOperand(counter.getAndIncrement(), this.format, a))
            ).map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }

    /**
     * Base of the instruction.
     * @return String base.
     */
    private String base() {
        return String.format("%s.%s", "opcode", new OpcodeName(this.opcode).simplified());
    }

    /**
     * Instruction comment.
     * Later this message will be converted to the XML comment, like:
     * <!-- INVOKESPECIAL 183, "java/lang/Object", "<init>", "()V" -->
     * @return String comment.
     */
    private String comment() {
        return String.format(
            "#%d:%s(%s)",
            this.opcode,
            new OpcodeName(this.opcode).simplified(),
            Arrays.stream(this.arguments)
                .map(Object::toString)
                .collect(Collectors.joining(", "))
        );
    }
}
