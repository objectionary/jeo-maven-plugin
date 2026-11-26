/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests the public URL helpers.
 * @since 1.0-SNAPSHOT
 */
final class UrlEncodingTest {

    @Test
    void encodesSpacesAndLiteralPlusSigns() {
        MatcherAssert.assertThat(
            new UrlEncoder("a + b/é").encode(),
            Matchers.equalTo("a+%2B+b%2F%C3%A9")
        );
    }

    @Test
    void decodesUtfEightAndDistinguishesSpacesFromPlusSigns() {
        MatcherAssert.assertThat(
            new UrlDecoder("a+%2B+b%2F%C3%A9").decode(),
            Matchers.equalTo("a + b/é")
        );
    }
}
