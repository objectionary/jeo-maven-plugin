/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eolang.jeo.representation.MethodName;
import org.eolang.jeo.representation.NumberedName;
import org.eolang.jeo.representation.PrefixedName;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives Method.
 * @since 0.1
 * @todo #534:90min Refactor DirectivesMethod class to simplify the code.
 *  Currently, the class has a lot of methods and fields. It's kind of hard to understand
 *  what this class does. We need to refactor it to make it more readable and understandable.
 *  Moreover, in many places this class uses null checks, which is not good.
 */
public final class DirectivesMethod implements Iterable<Directive> {

    /**
     * Method name.
     */
    private final NumberedName name;

    /**
     * Method properties.
     */
    private final DirectivesMethodProperties properties;

    /**
     * Method instructions.
     */
    private final List<Iterable<Directive>> instructions;

    /**
     * Method exceptions.
     */
    private final List<Iterable<Directive>> exceptions;

    /**
     * Method annotations.
     */
    private final DirectivesAnnotations annotations;

    /**
     * Default value.
     */
    private final List<Iterable<Directive>> dvalue;

    /**
     * Method attributes.
     */
    private final DirectivesAttributes attributes;

    /**
     * Constructor.
     * @param name Method name
     */
    public DirectivesMethod(final String name) {
        this(name, new DirectivesMethodProperties());
    }

    /**
     * Constructor.
     * @param name Method name
     * @param properties Method properties
     */
    public DirectivesMethod(
        final String name,
        final DirectivesMethodProperties properties
    ) {
        this(
            new NumberedName(1, new MethodName(name).xmir()),
            properties,
            new ArrayList<>(0),
            new ArrayList<>(0),
            new DirectivesAnnotations(),
            new ArrayList<>(0),
            new DirectivesAttributes()
        );
    }

    /**
     * Constructor.
     * @param name Method name
     * @param properties Method properties
     * @param instructions Method instructions
     * @param exceptions Method exceptions
     * @param annotations Method annotations
     * @param dvalue Annotation default value
     * @param attributes Method attributes
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public DirectivesMethod(
        final NumberedName name,
        final DirectivesMethodProperties properties,
        final List<Iterable<Directive>> instructions,
        final List<Iterable<Directive>> exceptions,
        final DirectivesAnnotations annotations,
        final List<Iterable<Directive>> dvalue,
        final DirectivesAttributes attributes
    ) {
        this.name = name;
        this.properties = properties;
        this.instructions = instructions;
        this.exceptions = exceptions;
        this.annotations = annotations;
        this.dvalue = dvalue;
        this.attributes = attributes;
    }

    /**
     * Add opcode to the directives.
     * @param opcode Opcode
     * @param operands Operands
     * @return This object
     */
    public DirectivesMethod withOpcode(final int opcode, final Object... operands) {
        this.instructions.add(new DirectivesInstruction(opcode, operands));
        return this;
    }

    /**
     * Add annotation to the directives.
     * @param annotation Annotation directives.
     * @return This object.
     */
    public DirectivesMethod withAnnotation(final DirectivesAnnotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    @Override
    public Iterator<Directive> iterator() {
        final String mname = this.name.toString();
        return new DirectivesJeoObject(
            "method",
            new PrefixedName(mname).encode(),
            Stream.concat(
                Stream.of(
                    this.properties,
                    this.annotations,
                    new DirectivesOptionalSeq("body", this.instructions),
                    new DirectivesOptionalSeq(
                        String.format("trycatchblocks-%s", this.name.plain()),
                        this.exceptions
                    )
                ),
                Stream.concat(
                    this.dvalue.stream(),
                    Stream.of(
                        this.attributes,
                        new DirectivesValue("name", mname)
                    )
                )
            ).map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }
}
