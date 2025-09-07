/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import java.util.Optional;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Directives for a method parameter.
 * @since 0.6
 */
public final class DirectivesMethodParam implements Iterable<Directive> {

    /**
     * Directives format.
     */
    private final Format format;

    /**
     * Index of the parameter.
     */
    private final int index;

    /**
     * Name of the parameter.
     */
    private final String name;

    /**
     * Method parameter access modifier.
     */
    private final int access;

    /**
     * Type of the parameter.
     */
    private final Type type;

    /**
     * Constructor.
     * @param format Directives format.
     * @param index Index of the parameter.
     * @param name Name of the parameter.
     * @param access Access modifier of the parameter.
     * @param type Type of the parameter.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesMethodParam(
        final Format format,
        final int index,
        final String name,
        final int access,
        final Type type
    ) {
        this.format = format;
        this.index = index;
        this.name = name;
        this.access = access;
        this.type = type;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "param",
            Optional.ofNullable(this.name).orElse(String.format("arg%d", this.index)),
            new DirectivesValue(this.format, "name", this.name),
            new DirectivesValue(this.format, "index", this.index),
            new DirectivesValue(this.format, "access", this.access),
            new DirectivesValue(this.format, "type", this.type.toString())
        ).iterator();
    }
}
