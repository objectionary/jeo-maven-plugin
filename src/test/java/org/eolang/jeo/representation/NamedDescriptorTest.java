/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test for {@link NamedDescriptor}.
 * @since 0.5
 */
final class NamedDescriptorTest {

    @ParameterizedTest(name = "Encodes \"{0}\" name with \"{1}\" descriptor, should be \"{2}\"")
    @MethodSource("namesDescriptorsAndEncoded")
    void encodesNamesWithDescriptors(final String name, final String descr, final String encoded) {
        MatcherAssert.assertThat(
            "Encoded method name and descriptor should be correct",
            new NamedDescriptor(name, descr).encoded(),
            Matchers.equalTo(encoded)
        );
    }

    @ParameterizedTest(name = "Decodes \"{2}\" with \"{1}\" descriptor, should be \"{0}\"")
    @MethodSource("namesDescriptorsAndEncoded")
    void decodesNames(final String name, final String descr, final String encoded) {
        MatcherAssert.assertThat(
            "Decoded method name should be correct",
            new NamedDescriptor(encoded).name(),
            Matchers.equalTo(name)
        );
    }

    @ParameterizedTest(name = "Decodes \"{2}\" with \"{1}\" descriptor")
    @MethodSource("namesDescriptorsAndEncoded")
    void decodesDescriptors(final String name, final String descr, final String encoded) {
        MatcherAssert.assertThat(
            "Decoded method descriptor should be correct",
            new NamedDescriptor(encoded).descriptor(),
            Matchers.equalTo(descr)
        );
    }

    /**
     * Provides names, descriptors and encoded values.
     * This method is used by {@link #encodesNamesWithDescriptors(String, String, String)},
     * {@link #decodesNames(String, String, String)}
     * and {@link #decodesDescriptors(String, String, String)}.
     * @return Arguments for tests.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> namesDescriptorsAndEncoded() {
        return Stream.of(
            Arguments.of("foo", "()I", "foo-%28%29I"),
            Arguments.of("@init@", "()V", "@init@-%28%29V"),
            Arguments.of("bar", "(Ljava/lang/String;)V", "bar-%28Ljava%2Flang%2FString%3B%29V"),
            Arguments.of(
                "baz",
                "(Ljava/lang/String;Ljava/lang/String;)V",
                "baz-%28Ljava%2Flang%2FString%3BLjava%2Flang%2FString%3B%29V"
            )
        );
    }
}
