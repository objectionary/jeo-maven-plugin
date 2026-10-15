/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Byte array ownership in bytecode values.
 *
 * @since 0.0.0
 */
final class BytecodeArraySnapshotTest {

    @Test
    void encodesTheOriginalBytesAfterTheInputChanges() {
        final byte[] input = {1, 2};
        final BytecodeValue value = new BytecodeValue(input);
        input[0] = 9;
        MatcherAssert.assertThat(
            "Encoding retained the caller's mutable array",
            value.encode(new JavaCodec()), Matchers.equalTo(new byte[]{1, 2})
        );
    }

    @Test
    void keepsTheValueWhenReturnedArraysChange() {
        final BytecodeValue value = new BytecodeValue(new byte[]{1, 2});
        ((byte[]) value.value())[0] = 9;
        value.encode(new JavaCodec())[1] = 8;
        MatcherAssert.assertThat(
            "A getter or encoded result exposed the stored byte array",
            value.encode(new JavaCodec()), Matchers.equalTo(new byte[]{1, 2})
        );
    }

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
