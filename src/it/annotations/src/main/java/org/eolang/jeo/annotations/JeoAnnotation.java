/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JeoAnnotation {
    String name();

    int value();

    boolean required() default false;

    MyEnum color();

    String[] tags();

    int[] ints();

    long[] longs();

    double[] doubles();

    float[] floats();

    Class<?>[] classes();

    NestedAnnotation nested();

    NestedAnnotation[] nestedArray();

    InnerEnum innerEnum() default InnerEnum.ONE;

    NestedAnnotation nestedEmpty();
    enum InnerEnum {
        ONE, TWO, THREE
    }
}
