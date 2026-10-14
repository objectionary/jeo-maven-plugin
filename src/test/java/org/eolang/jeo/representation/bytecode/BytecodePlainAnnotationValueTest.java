/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Equality of plain annotation values.
 *
 * @since 0.6
 */
final class BytecodePlainAnnotationValueTest {

    @ParameterizedTest
    @MethodSource("arrays")
    void comparesPrimitiveArraysByContents(final Object array) {
        final BytecodePlainAnnotationValue first = new BytecodePlainAnnotationValue("items", array);
        final BytecodePlainAnnotationValue second = new BytecodePlainAnnotationValue(
            "items", BytecodePlainAnnotationValueTest.copy(array)
        );
        MatcherAssert.assertThat(
            "Independent arrays with the same contents have equal values and hashes",
            Arrays.asList(first.equals(second), first.hashCode() == second.hashCode()),
            Matchers.contains(true, true)
        );
    }

    @ParameterizedTest
    @MethodSource("arrays")
    void distinguishesDifferentArrayContents(final Object array) {
        final Object changed = BytecodePlainAnnotationValueTest.copy(array);
        Array.set(changed, 0, Array.get(array, 1));
        MatcherAssert.assertThat(
            "Different primitive contents describe different annotation values",
            new BytecodePlainAnnotationValue("items", array),
            Matchers.not(Matchers.equalTo(new BytecodePlainAnnotationValue("items", changed)))
        );
    }

    @ParameterizedTest
    @MethodSource("arrays")
    void distinguishesPropertyNames(final Object array) {
        MatcherAssert.assertThat(
            "Equal arrays under different names describe different properties",
            new BytecodePlainAnnotationValue("first", array),
            Matchers.not(Matchers.equalTo(new BytecodePlainAnnotationValue("second", array)))
        );
    }

    @ParameterizedTest
    @CsvSource({"alpha,alpha,true", "alpha,beta,false"})
    void retainsScalarEquality(final String first, final String second, final boolean equal) {
        MatcherAssert.assertThat(
            "Scalar values keep their previous comparison",
            new BytecodePlainAnnotationValue("text", first).equals(
                new BytecodePlainAnnotationValue("text", second)
            ),
            Matchers.equalTo(equal)
        );
    }

    private static Object copy(final Object array) {
        final Object result = Array.newInstance(
            array.getClass().getComponentType(), Array.getLength(array)
        );
        System.arraycopy(array, 0, result, 0, Array.getLength(array));
        return result;
    }

    private static Stream<Object> arrays() {
        return Stream.of(
            new boolean[]{true, false}, new byte[]{1, 2}, new char[]{'a', 'b'},
            new short[]{1, 2}, new int[]{1, 2}, new long[]{1L, 2L},
            new float[]{1.0f, 2.0f}, new double[]{1.0d, 2.0d}
        );
    }
}
