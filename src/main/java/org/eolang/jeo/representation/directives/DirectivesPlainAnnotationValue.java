/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.IntStream;
import org.xembly.Directive;

/**
 * An annotation value that is plain.
 *
 * @since 0.6
 */
public final class DirectivesPlainAnnotationValue implements Iterable<Directive> {

    /**
     * Index of the annotation value among other annotation values.
     */
    private final int index;

    /**
     * The format of the directives.
     */
    private final Format format;

    /**
     * The name of the annotation property.
     */
    private final String name;

    /**
     * The actual value.
     */
    private final Object value;

    /**
     * Constructor.
     */
    DirectivesPlainAnnotationValue() {
        this(0, new Format(), "", "");
    }

    /**
     * Constructor.
     *
     * @param index Index of the annotation value among other annotation values
     * @param format The format of the directives
     * @param name The name of the annotation property
     * @param value The actual value
     */
    public DirectivesPlainAnnotationValue(
        final int index,
        final Format format,
        final String name,
        final Object value
    ) {
        this.index = index;
        this.format = format;
        this.name = name;
        this.value = value;
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterable<Directive> res;
        if (Arrays.stream(
            new Class<?>[]{
                byte[].class,
                short[].class,
                int[].class,
                long[].class,
                float[].class,
                double[].class,
                boolean[].class,
                char[].class,
                Integer[].class,
                Long[].class,
                Float[].class,
                Double[].class,
                Boolean[].class,
                Character[].class,
                String[].class,
                Class[].class,
                Object[].class,
            }
        ).anyMatch(iter -> iter.equals(this.value.getClass()))) {
            res = new DirectivesValues(
                this.format,
                new NumName("a", 2).toString(),
                IntStream.range(0, Array.getLength(this.value))
                    .mapToObj(pos -> Array.get(this.value, pos))
                    .toArray()
            );
        } else {
            res = new DirectivesOperand(2, this.format, this.value);
        }
        return new DirectivesJeoObject(
            "annotation-property",
            new NumName("p", this.index).toString(),
            new DirectivesValue(0, this.format, "PLAIN"),
            new DirectivesValue(1, this.format, Optional.ofNullable(this.name).orElse("")),
            res
        ).iterator();
    }
}
