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
     * @param index Index of the local variable in the local variable array.
     * @param name Name of the local variable.
     * @param descriptor Descriptor of the local variable.
     * @param signature Signature of the local variable.
     * @param start Start directives for the local variable, e.g. labels.
     * @param end End directives for the local variable, e.g. labels.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesLocalVariables(
        final int index,
        final String name,
        final String descriptor,
        final String signature,
        final Iterable<Directive> start,
        final Iterable<Directive> end
    ) {
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
            new DirectivesValue("index", this.index),
            new DirectivesValue("name", this.name),
            new DirectivesValue("descr", this.descriptor),
            new DirectivesValue("signature",  Optional.ofNullable(this.signature).orElse("")),
            this.start,
            this.end
        ).iterator();
    }
}
