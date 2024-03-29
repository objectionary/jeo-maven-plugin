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

    MyEnum color();

    String[] tags();

    int[] ints();

    long[] longs();

    double[] doubles();

    float[] floats();
}