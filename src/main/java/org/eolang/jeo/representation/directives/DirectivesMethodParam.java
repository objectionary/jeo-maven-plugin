/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Iterator;
import org.objectweb.asm.Type;
import org.xembly.Directive;

/**
 * Directives for a method parameter.
 * @since 0.6
 */
public final class DirectivesMethodParam implements Iterable<Directive> {

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
     * Annotations of the parameter.
     */
    private final DirectivesAnnotations annotations;

    /**
     * Constructor.
     * @param index Index of the parameter.
     * @param name Name of the parameter.
     * @param access Access modifier of the parameter.
     * @param type Type of the parameter.
     * @param annotations Annotations of the parameter.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DirectivesMethodParam(
        final int index,
        final String name,
        final int access,
        final Type type,
        final DirectivesAnnotations annotations
    ) {
        this.index = index;
        this.name = name;
        this.access = access;
        this.type = type;
        this.annotations = annotations;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new DirectivesJeoObject(
            "param",
            this.name,
            new DirectivesValue("index", this.index),
            new DirectivesValue("access", this.access),
            new DirectivesValue("type", this.type.toString()),
            this.annotations
        ).iterator();
    }
}
