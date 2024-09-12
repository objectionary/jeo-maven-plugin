/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Annotation property as Xembly directives.
 * @since 0.3
 * @todo #537:60min Refactor {@link DirectivesAnnotationProperty} class.
 *  The class uses public static methods to create instances of itself.
 *  We should use constructors instead of static methods.
 *  Don't forget to add/update the tests.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class DirectivesAnnotationProperty implements Iterable<Directive> {

    /**
     * Type of the property.
     */
    private final Type type;

    /**
     * Property parameters.
     */
    private final List<Iterable<Directive>> params;

    /**
     * Constructor.
     * @param type Type of the property.
     * @param params Property parameters.
     */
    @SafeVarargs
    public DirectivesAnnotationProperty(final Type type, final Iterable<Directive>... params) {
        this(type, Arrays.asList(params));
    }

    /**
     * Constructor.
     * @param type Type of the property.
     * @param params Property parameters.
     */
    private DirectivesAnnotationProperty(final Type type, final List<Iterable<Directive>> params) {
        this.type = type;
        this.params = new ArrayList<>(params);
    }

    /**
     * Factory method for plain property.
     * @param name Name.
     * @param value Parameter.
     * @return Property directives.
     */
    @SuppressWarnings("PMD.ProhibiyPublicStaticMethods")
    public static DirectivesAnnotationProperty plain(final String name, final Object value) {
        final DirectivesAnnotationProperty result;
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
            final DirectivesTypedTuple res;
            if (value.getClass().equals(int[].class)) {
                res = new DirectivesTypedTuple("", (int[]) value);
            } else if (value.getClass().equals(long[].class)) {
                res = new DirectivesTypedTuple("", (long[]) value);
            } else if (value.getClass().equals(float[].class)) {
                res = new DirectivesTypedTuple("", (float[]) value);
            } else if (value.getClass().equals(double[].class)) {
                res = new DirectivesTypedTuple("", (double[]) value);
            } else if (value.getClass().equals(boolean[].class)) {
                res = new DirectivesTypedTuple("", (boolean[]) value);
            } else if (value.getClass().equals(char[].class)) {
                res = new DirectivesTypedTuple("", (char[]) value);
            } else if (value.getClass().equals(byte[].class)) {
                res = new DirectivesTypedTuple("", (byte[]) value);
            } else if (value.getClass().equals(short[].class)) {
                res = new DirectivesTypedTuple("", (short[]) value);
            } else {
                res = new DirectivesTypedTuple("", (Object[]) value);
            }
            result = new DirectivesAnnotationProperty(
                Type.PLAIN,
                new DirectivesData(Optional.ofNullable(name).orElse("")),
                res
            );
        } else {
            result = new DirectivesAnnotationProperty(
                Type.PLAIN,
                new DirectivesData(Optional.ofNullable(name).orElse("")),
                new DirectivesData(value)
            );
        }
        return result;
    }

    /**
     * Factory method for enum property.
     * @param name Name.
     * @param descriptor Descriptor.
     * @param value Value.
     * @return Property directives.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static DirectivesAnnotationProperty enump(
        final String name, final String descriptor, final String value
    ) {
        return new DirectivesAnnotationProperty(
            Type.ENUM,
            new DirectivesData(Optional.ofNullable(name).orElse("")),
            new DirectivesData(descriptor),
            new DirectivesData(value)
        );
    }

    /**
     * Factory method for array property.
     * @param name Name.
     * @param child Child directives.
     * @return Property directives.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static DirectivesAnnotationProperty array(
        final String name, final Iterable<Directive> child
    ) {
        return new DirectivesAnnotationProperty(
            Type.ARRAY,
            new DirectivesData(Optional.ofNullable(name).orElse("")),
            child
        );
    }

    /**
     * Factory method for array property.
     * @param name Name.
     * @param properties Child directives.
     * @return Property directives.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static DirectivesAnnotationProperty array(
        final String name, List<Iterable<Directive>> properties
    ) {
        return new DirectivesAnnotationProperty(
            Type.ARRAY,
            properties.stream()
                .map(Directives::new)
                .reduce(
                    new Directives()
                        .append(new DirectivesData(Optional.ofNullable(name).orElse(""))),
                    Directives::append
                )
        );
    }

    /**
     * Factory method for annotation property.
     * @param name Name.
     * @param descriptor Descriptor.
     * @param child Child directives.
     * @return Property directives.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static DirectivesAnnotationProperty annotation(
        final String name, final String descriptor, final Iterable<Directive> child
    ) {
        return new DirectivesAnnotationProperty(
            Type.ANNOTATION,
            new DirectivesData(Optional.ofNullable(name).orElse("")),
            new DirectivesData(descriptor),
            child
        );
    }

    /**
     * Factory method for annotation property.
     * @param name Name.
     * @param descriptor Descriptor.
     * @return Property directives.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static DirectivesAnnotationProperty annotation(
        final String name, final String descriptor, List<Iterable<Directive>> properties
    ) {
        return new DirectivesAnnotationProperty(
            Type.ANNOTATION,
            new DirectivesData(Optional.ofNullable(name).orElse("")),
            properties.stream().map(Directives::new)
                .reduce(new Directives().append(new DirectivesData(descriptor)), Directives::append)
        );
    }

    @Override
    public Iterator<Directive> iterator() {
        final Directives directives = new Directives()
            .add("o").attr("base", "annotation-property")
            .append(new DirectivesData(this.type.toString()));
        this.params.forEach(directives::append);
        return directives.up().iterator();
    }

    /**
     * Property types.
     */
    enum Type {
        /**
         * Plain property.
         */
        PLAIN,

        /**
         * Enum property.
         */
        ENUM,

        /**
         * Array property.
         */
        ARRAY,

        /**
         * Annotation property.
         */
        ANNOTATION
    }
}
