/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
import org.xembly.Directives;

public final class DirectivesPlainAnnotationValue implements Iterable<Directive> {

    private final String name;
    private final Object value;

    public DirectivesPlainAnnotationValue() {
        this("", "");
    }

    public DirectivesPlainAnnotationValue(final String name, final Object value) {
        this.name = name;
        this.value = value;
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
        if (Arrays.stream(iterable).anyMatch(iter -> iter.equals(value.getClass()))) {
            if (value.getClass().equals(int[].class)) {
                res = new DirectivesValues("", (int[]) value);
            } else if (value.getClass().equals(long[].class)) {
                res = new DirectivesValues("", (long[]) value);
            } else if (value.getClass().equals(float[].class)) {
                res = new DirectivesValues("", (float[]) value);
            } else if (value.getClass().equals(double[].class)) {
                res = new DirectivesValues("", (double[]) value);
            } else if (value.getClass().equals(boolean[].class)) {
                res = new DirectivesValues("", (boolean[]) value);
            } else if (value.getClass().equals(char[].class)) {
                res = new DirectivesValues("", (char[]) value);
            } else if (value.getClass().equals(byte[].class)) {
                res = new DirectivesValues("", (byte[]) value);
            } else if (value.getClass().equals(short[].class)) {
                res = new DirectivesValues("", (short[]) value);
            } else {
                res = new DirectivesValues("", (Object[]) value);
            }
//            result = new DirectivesAnnotationProperty(
//                DirectivesAnnotationProperty.Type.PLAIN,
//                new DirectivesValue(Optional.ofNullable(name).orElse("")),
//                res
//            );
        } else {
//            result = new DirectivesAnnotationProperty(
//                DirectivesAnnotationProperty.Type.PLAIN,
//                new DirectivesValue(Optional.ofNullable(name).orElse("")),
//                new DirectivesValue(this.value)
//            );
            res = new DirectivesValue(this.value);
        }
        return new Directives()
            .add("o").attr("base", new JeoFqn("annotation-property").fqn())
            .append(new DirectivesValue("PLAIN"))
            .append(new DirectivesValue(Optional.ofNullable(this.name).orElse("")))
            .append(res)
            .up()
            .iterator();
    }
}
