/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.xembly.Directive;

/**
 * Directives Record Component.
 * <p>All the directives in {@link DirectivesRecordComponent} are sorted according to the JVM
 * specification:
 * {@code
 * record_component_info {
 *     u2             name_index;
 *     u2             descriptor_index;
 *     u2             attributes_count;
 *     attribute_info attributes[attributes_count];
 * }}
 * </p>
 * @since 0.15.0
 */
public final class DirectivesRecordComponent implements Iterable<Directive> {

    /**
     * Directives format.
     */
    private final Format format;

    /**
     * Index.
     */
    private final int index;

    /**
     * Name.
     */
    private final String name;

    /**
     * Descriptor.
     */
    private final String descriptor;

    /**
     * Signature.
     */
    private final String signature;

    /**
     * Bytecode annotations.
     */
    private final DirectivesAnnotations annotations;

    /**
     * Type annotations.
     */
    private final DirectivesTypeAnnotations types;

    /**
     * Constructor.
     * @param format Directives format.
     * @param index Component index.
     * @param name Component name.
     * @param descriptor Descriptor.
     * @param signature Signature.
     * @param annotations Bytecode annotations.
     * @param types Type annotations.
     * @checkstyle ParameterNumber (10 lines)
     */
    public DirectivesRecordComponent(
        final Format format,
        final int index,
        final String name,
        final String descriptor,
        final String signature,
        final DirectivesAnnotations annotations,
        final DirectivesTypeAnnotations types
    ) {
        this.format = format;
        this.index = index;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.annotations = annotations;
        this.types = types;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "record-component",
            String.format("rc%d", this.index),
            new DirectivesValue(this.format, "name", this.name),
            new DirectivesValue(this.format, "descriptor", this.descriptor),
            new DirectivesValue(this.format, "signature", this.signature),
            this.annotations,
            this.types
        ).iterator();
    }
}
