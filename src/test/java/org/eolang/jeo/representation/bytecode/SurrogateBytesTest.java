/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link SurrogateBytes}.
 *
 * @since 0.18.0
 */
final class SurrogateBytesTest {

    @Test
    void decodesLoneSurrogate() {
        MatcherAssert.assertThat(
            "Three bytes of a lone surrogate must become that code unit again",
            new SurrogateBytes(new byte[]{0x61, (byte) 0xED, (byte) 0xA0, (byte) 0x80}).text(),
            Matchers.equalTo(String.format("a%c", 0xD800))
        );
    }

    @Test
    void replacesBrokenByte() {
        MatcherAssert.assertThat(
            "A byte that starts no sequence must become U+FFFD",
            new SurrogateBytes(new byte[]{(byte) 0xFF, 0x41}).text(),
            Matchers.equalTo(String.format("%cA", 0xFFFD))
        );
    }
}
