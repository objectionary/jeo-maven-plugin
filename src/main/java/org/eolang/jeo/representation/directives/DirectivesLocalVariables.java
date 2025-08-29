/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Optional;
import org.xembly.Directive;

/**
 * Directives for local variables in a method.
 * @since 0.14.0
 */
public final class DirectivesLocalVariables implements Iterable<Directive> {

    /**
     * Ordered index.
     * This is used to generate unique names for the directives.
     * Nothing more, nothing less.
     * We cannot use index of the local variable, because in some cases it's not unique.
     */
    private final int oindex;

    /**
     * Directives format.
     */
    private final Format format;

    /**
     * Index of the local variable in the local variable array.
     */
    private final int index;

    /**
     * Name of the local variable.
     */
    private final String name;

    /**
     * Descriptor of the local variable.
     */
    private final String descriptor;

    /**
     * Signature of the local variable.
     */
    private final String signature;

    /**
     * Start label.
     */
    private final Iterable<Directive> start;

    /**
     * Start label.
     */
    private final Iterable<Directive> end;

    /**
     * Constructor.
     * @param oindex Ordered index of the local variable.
     * @param format Directives format.
     * @param index Index of the local variable in the local variable array.
     * @param name Name of the local variable.
     * @param descriptor Descriptor of the local variable.
     * @param signature Signature of the local variable.
     * @param start Start directives for the local variable, e.g. labels.
     * @param end End directives for the local variable, e.g. labels.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesLocalVariables(
        final int oindex,
        final Format format,
        final int index,
        final String name,
        final String descriptor,
        final String signature,
        final Iterable<Directive> start,
        final Iterable<Directive> end
    ) {
        this.oindex = oindex;
        this.format = format;
        this.index = index;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.start = start;
        this.end = end;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesAttribute(
            "local-variable",
            new NumName("a", this.oindex).toString(),
            new DirectivesValue(this.format, "index", this.index),
            new DirectivesValue(this.format, "name", this.name),
            new DirectivesValue(this.format, "descr", this.descriptor),
            new DirectivesValue(
                this.format, "signature", Optional.ofNullable(this.signature).orElse("")
            ),
            this.start,
            this.end
        ).iterator();
    }
}
