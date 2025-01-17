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
package org.eolang.jeo.representation;

import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test for {@link Signature}.
 * @since 0.5
 */
final class SignatureTest {

    @ParameterizedTest(name = "Encodes \"{0}\" name with \"{1}\" descriptor, should be \"{2}\"")
    @MethodSource("namesDescriptorsAndEncoded")
    void encodesNamesWithDescriptors(final String name, final String descr, final String encoded) {
        MatcherAssert.assertThat(
            "Encoded method name and descriptor should be correct",
            new Signature(name, descr).encoded(),
            Matchers.equalTo(encoded)
        );
    }

    @ParameterizedTest(name = "Decodes \"{2}\" with \"{1}\" descriptor, should be \"{0}\"")
    @MethodSource("namesDescriptorsAndEncoded")
    void decodesNames(final String name, final String descr, final String encoded) {
        MatcherAssert.assertThat(
            "Decoded method name should be correct",
            new Signature(encoded).name(),
            Matchers.equalTo(name)
        );
    }

    @ParameterizedTest(name = "Decodes \"{2}\" with \"{1}\" descriptor")
    @MethodSource("namesDescriptorsAndEncoded")
    void decodesDescriptors(final String name, final String descr, final String encoded) {
        MatcherAssert.assertThat(
            "Decoded method descriptor should be correct",
            new Signature(encoded).descriptor(),
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
