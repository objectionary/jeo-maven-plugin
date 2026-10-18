/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Byte array ownership in decoded bytecode bytes.
 *
 * @since 0.0.0
 */
final class BytecodeBytesTest {

    @Test
    void decodesTheOriginalBytesAfterInputAndOutputChanges() {
        final byte[] input = {1, 2};
        final BytecodeBytes bytes = new BytecodeBytes("bytes", input);
        input[0] = 9;
        ((byte[]) bytes.object(new JavaCodec()))[1] = 8;
        MatcherAssert.assertThat(
            "Decoding exposed the constructor's or the stored byte array",
            bytes.object(new JavaCodec()), Matchers.equalTo(new byte[]{1, 2})
        );
    }
}
