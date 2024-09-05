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
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test case for {@link PrefixedName}.
 * @since 0.1
 */
final class PrefixedNameTest {

    @ParameterizedTest
    @CsvSource({
        "name, j$name",
        "ClassName, j$ClassName",
        "someLongName, j$someLongName",
        "j$j, j$j$j",
        "jeo/xmir/Fake, jeo/xmir/j$Fake"
    })
    void encodesName(final String origin, final String encoded) {
        MatcherAssert.assertThat(
            String.format("Can't encode '%s' to '%s'", origin, encoded),
            new PrefixedName(origin).encode(),
            Matchers.equalTo(encoded)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "j$name, name",
        "j$ClassName, ClassName",
        "j$someLongName, someLongName",
        "j$j$j, j$j",
        "someName, someName",
        "jeo/xmir/j$Fake, jeo/xmir/Fake"
    })
    void decodesName(final String encoded, final String origin) {
        MatcherAssert.assertThat(
            String.format("Can't decode '%s' to '%s'", encoded, origin),
            new PrefixedName(encoded).decode(),
            Matchers.equalTo(origin)
        );
    }

    @Test
    void throwsExceptionWhenEncodingInvalidName() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new PrefixedName(" ").encode(),
            "Can't throw exception when encoding invalid name"
        );
    }

    @Test
    void throwsExceptionWhenDecodingInvalidName() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new PrefixedName(" ").decode(),
            "Can't throw exception when decoding invalid name"
        );
    }
}
