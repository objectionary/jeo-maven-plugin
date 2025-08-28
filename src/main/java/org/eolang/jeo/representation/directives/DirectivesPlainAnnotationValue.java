/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.directives;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import org.xembly.Directive;

/**
 * An annotation value that is plain.
 * @since 0.6
 */
public final class DirectivesPlainAnnotationValue implements Iterable<Directive> {

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
     * @param format The format of the directives.
     * @param name The name of the annotation property.
     * @param value The actual value.
     */
    public DirectivesPlainAnnotationValue(
        final Format format, final String name, final Object value
    ) {
        this.format = format;
        this.name = name;
        this.value = value;
    }

    /**
     * Constructor.
     */
    DirectivesPlainAnnotationValue() {
        this(new Format(), "", "");
    }

    @Override
    public Iterator<Directive> iterator() {
        final Iterable<Directive> res;
        final Class<?>[] iterable = {
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
        };
        if (Arrays.stream(iterable).anyMatch(iter -> iter.equals(this.value.getClass()))) {
            if (this.value.getClass().equals(int[].class)) {
                res = new DirectivesValues(this.format, "", (int[]) this.value);
            } else if (this.value.getClass().equals(long[].class)) {
                res = new DirectivesValues(this.format, "", (long[]) this.value);
            } else if (this.value.getClass().equals(float[].class)) {
                res = new DirectivesValues(this.format, "", (float[]) this.value);
            } else if (this.value.getClass().equals(double[].class)) {
                res = new DirectivesValues(this.format, "", (double[]) this.value);
            } else if (this.value.getClass().equals(boolean[].class)) {
                res = new DirectivesValues(this.format, "", (boolean[]) this.value);
            } else if (this.value.getClass().equals(char[].class)) {
                res = new DirectivesValues(this.format, "", (char[]) this.value);
            } else if (this.value.getClass().equals(byte[].class)) {
                res = new DirectivesValues(this.format, "", (byte[]) this.value);
            } else if (this.value.getClass().equals(short[].class)) {
                res = new DirectivesValues(this.format, "", (short[]) this.value);
            } else {
                res = new DirectivesValues(this.format, "", (Object[]) this.value);
            }
        } else {
            res = new DirectivesOperand(this.format, this.value);
        }
        return new DirectivesJeoObject(
            "annotation-property",
            new RandName("p").toString(),
            new DirectivesValue(this.format, "PLAIN"),
            new DirectivesValue(this.format, Optional.ofNullable(this.name).orElse("")),
            res
        ).iterator();
    }
}
