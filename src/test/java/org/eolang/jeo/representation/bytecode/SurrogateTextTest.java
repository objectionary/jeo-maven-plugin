/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link SurrogateText}.
 *
 * @since 0.18.0
 */
final class SurrogateTextTest {

    @Test
    void encodesPairedTextLikeStandardUtf() {
        final String text = String.format("caf%c %c", 0xE9, 0x1F600);
        MatcherAssert.assertThat(
            "Text without lone surrogates must be encoded exactly as standard UTF-8",
            new SurrogateText(text).bytes(),
            Matchers.equalTo(text.getBytes(StandardCharsets.UTF_8))
        );
    }

    @Test
    void encodesLoneSurrogateInThreeBytes() {
        MatcherAssert.assertThat(
            "A lone surrogate must be written as its own three bytes, not as '?'",
            new SurrogateText(String.format("%c", 0xD800)).bytes(),
            Matchers.equalTo(new byte[]{(byte) 0xED, (byte) 0xA0, (byte) 0x80})
        );
    }
}
