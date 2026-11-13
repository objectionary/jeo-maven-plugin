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

    @Test
    void replacesOverlongTwoByteSequences() {
        MatcherAssert.assertThat(
            "Overlong 2-byte UTF-8 sequences must be replaced with U+FFFD",
            new SurrogateBytes(
                new byte[]{0x61, (byte) 0xC0, (byte) 0x80, (byte) 0xC1, (byte) 0xBF, 0x62}
            ).text(),
            Matchers.equalTo(String.format("a%c%c%c%cb", 0xFFFD, 0xFFFD, 0xFFFD, 0xFFFD))
        );
    }

    @Test
    void replacesOverlongThreeAndFourByteSequences() {
        MatcherAssert.assertThat(
            "Overlong 3-byte and 4-byte UTF-8 sequences must be replaced with U+FFFD",
            new SurrogateBytes(
                new byte[]{
                    (byte) 0xE0, (byte) 0x80, (byte) 0x80,
                    (byte) 0xE0, (byte) 0x9F, (byte) 0xBF,
                    (byte) 0xF0, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                    (byte) 0xF0, (byte) 0x8F, (byte) 0xBF, (byte) 0xBF,
                }
            ).text(),
            Matchers.equalTo(new String(new char[14]).replace('\0', (char) 0xFFFD))
        );
    }

    @Test
    void replacesCodePointsAboveUnicodeMax() {
        MatcherAssert.assertThat(
            "UTF-8 sequences decoding above U+10FFFF must be replaced with U+FFFD",
            new SurrogateBytes(
                new byte[]{
                    (byte) 0xF4, (byte) 0x90, (byte) 0x80, (byte) 0x80,
                    (byte) 0xF7, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF,
                }
            ).text(),
            Matchers.equalTo(new String(new char[8]).replace('\0', (char) 0xFFFD))
        );
    }

    @Test
    void decodesValidBoundaryAndSupplementaryCodePoints() {
        MatcherAssert.assertThat(
            "Valid boundary code points including U+0080, U+0800, U+10000, and U+10FFFF must decode",
            new SurrogateBytes(
                new byte[]{
                    (byte) 0xC2, (byte) 0x80,
                    (byte) 0xE0, (byte) 0xA0, (byte) 0x80,
                    (byte) 0xED, (byte) 0xBF, (byte) 0xBF,
                    (byte) 0xF0, (byte) 0x90, (byte) 0x80, (byte) 0x80,
                    (byte) 0xF4, (byte) 0x8F, (byte) 0xBF, (byte) 0xBF,
                }
            ).text(),
            Matchers.equalTo(
                new StringBuilder()
                    .appendCodePoint(0x80)
                    .appendCodePoint(0x800)
                    .appendCodePoint(0xDFFF)
                    .appendCodePoint(0x10000)
                    .appendCodePoint(0x10FFFF)
                    .toString()
            )
        );
    }
}
