/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
     * The name of the annotation property.
     */
    private final String name;

    /**
     * The actual value.
     */
    private final Object value;

    /**
     * Constructor.
     * @param name The name of the annotation property.
     * @param value The actual value.
     */
    public DirectivesPlainAnnotationValue(final String name, final Object value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Constructor.
     */
    DirectivesPlainAnnotationValue() {
        this("", "");
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
                res = new DirectivesValues("", (int[]) this.value);
            } else if (this.value.getClass().equals(long[].class)) {
                res = new DirectivesValues("", (long[]) this.value);
            } else if (this.value.getClass().equals(float[].class)) {
                res = new DirectivesValues("", (float[]) this.value);
            } else if (this.value.getClass().equals(double[].class)) {
                res = new DirectivesValues("", (double[]) this.value);
            } else if (this.value.getClass().equals(boolean[].class)) {
                res = new DirectivesValues("", (boolean[]) this.value);
            } else if (this.value.getClass().equals(char[].class)) {
                res = new DirectivesValues("", (char[]) this.value);
            } else if (this.value.getClass().equals(byte[].class)) {
                res = new DirectivesValues("", (byte[]) this.value);
            } else if (this.value.getClass().equals(short[].class)) {
                res = new DirectivesValues("", (short[]) this.value);
            } else {
                res = new DirectivesValues("", (Object[]) this.value);
            }
        } else {
            res = new DirectivesValue(this.value);
        }
        return new DirectivesJeoObject(
            "annotation-property",
            new DirectivesValue("PLAIN"),
            new DirectivesValue(Optional.ofNullable(this.name).orElse("")),
            res
        ).iterator();
    }
}
