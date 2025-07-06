/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
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
     * Constructor.
     * @param opcode Opcode
     * @param arguments Instruction arguments
     */
    public DirectivesInstruction(
        final int opcode,
        final Object... arguments
    ) {
        this.opcode = opcode;
        this.arguments = arguments.clone();
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            this.base(),
            new RandName("instruction").toString(),
            Stream.concat(
                Stream.of(
                    new DirectivesComment(this.comment()),
                    new DirectivesOperand(this.opcode)
                ),
                Arrays.stream(this.arguments).map(DirectivesOperand::new)
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
