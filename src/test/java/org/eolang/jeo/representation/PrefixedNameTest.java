/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test cases for {@link PrefixedName}.
 * This class verifies the functionality of prefixed name handling,
 * ensuring proper encoding and decoding of names with prefix conventions.
 *
 * @since 0.1.0
 */
final class PrefixedNameTest {

    @ParameterizedTest
    @CsvSource({
        "name, j$name",
        "ClassName, j$ClassName",
        "someLongName, j$someLongName",
        "j$j, j$j$j",
        "jeo/xmir/Fake, j$jeo/j$xmir/j$Fake",
        "EOorg.EOeolang, j$EOorg.j$EOeolang",
        "org.eolang, j$org.j$eolang",
        "org.eolang.Fake, j$org.j$eolang.j$Fake"
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
        "j$jeo/j$xmir/j$Fake, jeo/xmir/Fake",
        "j$EOorg.j$EOeolang, EOorg.EOeolang",
        "j$org.j$eolang, org.eolang",
        "j$org.j$eolang.j$Fake, org.eolang.Fake"
    })
    void decodesName(final String encoded, final String origin) {
        MatcherAssert.assertThat(
            String.format("Can't decode '%s' to '%s'", encoded, origin),
            new PrefixedName(encoded).decode(),
            Matchers.equalTo(origin)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "name, jm$name",
        "ClassName, jm$ClassName",
        "someLongName, jm$someLongName",
        "jm$jm, jm$jm$jm",
        "jeo/xmir/Fake, jm$jeo/jm$xmir/jm$Fake",
        "EOorg.EOeolang, jm$EOorg.jm$EOeolang",
        "org.eolang, jm$org.jm$eolang",
        "org.eolang.Fake, jm$org.jm$eolang.jm$Fake"
    })
    void encodesNamesWithDifferentPrefix(final String origin, final String encoded) {
        MatcherAssert.assertThat(
            String.format("Can't encode '%s' to '%s' with prefix 'jm'", origin, encoded),
            new PrefixedName("jm$", origin).encode(),
            Matchers.equalTo(encoded)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "jm$name, name",
        "jm$ClassName, ClassName",
        "jm$someLongName, someLongName",
        "jm$jm$jm, jm$jm",
        "someName, someName",
        "jm$jeo/jm$xmir/jm$Fake, jeo/xmir/Fake",
        "jm$EOorg.jm$EOeolang, EOorg.EOeolang",
        "jm$org.jm$eolang, org.eolang",
        "jm$org.jm$eolang.jm$Fake, org.eolang.Fake"
    })
    void decodesNamesWithDifferentPrefix(final String encoded, final String origin) {
        MatcherAssert.assertThat(
            String.format("Can't decode '%s' to '%s' with prefix 'jm'", encoded, origin),
            new PrefixedName("jm$", encoded).decode(),
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
