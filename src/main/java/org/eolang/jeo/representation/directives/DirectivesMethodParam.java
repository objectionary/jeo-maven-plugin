/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
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
            this.paramName(),
            new DirectivesValue(this.format, "name", this.name),
            new DirectivesValue(this.format, "index", this.index),
            new DirectivesValue(this.format, "access", this.access),
            new DirectivesValue(this.format, "type", this.type.toString())
        ).iterator();
    }

    /**
     * Get parameter name or a default one if it's null.
     * @return Parameter name
     */
    private String paramName() {
        final String prefix = "p";
        final String result;
        if (this.name == null) {
            result = String.format("%s%d", prefix, this.index);
        } else {
            result = String.format("%s%s", prefix, this.name);
        }
        return result;
    }
}
