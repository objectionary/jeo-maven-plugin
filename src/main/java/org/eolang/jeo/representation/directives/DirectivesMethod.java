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
 * All the directives are sorted according to JVM method specification:
 * {@code
 * method_info {
 *     u2             access_flags; {@link DirectivesMethodProperties}
 *     u2             name_index; {@link DirectivesMethodProperties}
 *     u2             descriptor_index; {@link DirectivesMethodProperties}
 *     u2             attributes_count; {@link DirectivesMethod}
 *     attribute_info attributes[attributes_count]; {@link DirectivesMethod}
 * }}
 * Pay attention, that most of the information about methods are stored into attributes:
 * - Body (Code)
 * - Annotations
 * - Try-catch blocks
 * - Default value (for annotation methods)
 * - And other attributes
 * @since 0.1
 * @todo #1183:60min Method name should should be moved to DirectivesMethodProperties
 *  Otherwise it breaks component ordering (see the JVM specification).
 *  The method name should be between access flags and descriptor - the both of them
 *  are defined the the {@link DirectivesMethodProperties}.
 */
public final class DirectivesMethod implements Iterable<Directive> {

    /**
     * Directives format.
     */
    private final Format format;

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
     * Method try-catch blocks.
     */
    private final List<Iterable<Directive>> tryblocks;

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
            new Format(),
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
     * @param format Directives format
     * @param name Method name
     * @param properties Method properties
     * @param instructions Method instructions
     * @param tryblocks Method try-catch blocks
     * @param annotations Method annotations
     * @param dvalue Annotation default value
     * @param attributes Method attributes
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public DirectivesMethod(
        final Format format,
        final NumberedName name,
        final DirectivesMethodProperties properties,
        final List<Iterable<Directive>> instructions,
        final List<Iterable<Directive>> tryblocks,
        final DirectivesAnnotations annotations,
        final List<Iterable<Directive>> dvalue,
        final DirectivesAttributes attributes
    ) {
        this.format = format;
        this.name = name;
        this.properties = properties;
        this.instructions = instructions;
        this.tryblocks = tryblocks;
        this.annotations = annotations;
        this.dvalue = dvalue;
        this.attributes = attributes;
    }

    /**
     * Add opcode to the directives.
     * @param index Instruction index
     * @param opcode Opcode
     * @param operands Operands
     * @return This object
     */
    public DirectivesMethod withOpcode(
        final int index, final int opcode, final Object... operands
    ) {
        this.instructions.add(new DirectivesInstruction(index, this.format, opcode, operands));
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
            new PrefixedName("jm$", mname).encode(),
            Stream.concat(
                Stream.of(
                    this.properties,
                    this.annotations,
                    new DirectivesSeq("body", this.instructions),
                    new DirectivesSeq("trycatchblocks", this.tryblocks)
                ),
                Stream.concat(
                    this.dvalue.stream(),
                    Stream.of(
                        this.attributes,
                        new DirectivesValue(this.format, "name", mname)
                    )
                )
            ).map(Directives::new).collect(Collectors.toList())
        ).iterator();
    }
}
