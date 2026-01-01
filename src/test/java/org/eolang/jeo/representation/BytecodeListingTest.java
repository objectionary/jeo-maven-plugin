/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import org.cactoos.bytes.BytesOf;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link BytecodeListing}.
 * @since 0.6
 */
final class BytecodeListingTest {

    @Test
    void convertsToPrettyString() throws Exception {
        MatcherAssert.assertThat(
            "Bytecode listing is not as expected",
            new BytecodeListing(
                new BytesOf(new ResourceOf("MethodByte.class")).asBytes()
            ).toString(),
            Matchers.containsString("org/eolang/jeo/MethodByte")
        );
    }
}
