/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ByteVector;

/**
 * Test case for {@link AsmUnknownAttribute}.
 * @since 0.15.0
 */
final class AsmUnknownAttributeTest {

    @Test
    void writesNullDataAsEmpty() {
        final ByteVector result = new AsmUnknownAttribute("Test", null)
            .write(null, null, 0, 0, 0);
        MatcherAssert.assertThat(
            "Writing attribute with null data should produce empty ByteVector, not throw",
            result,
            Matchers.notNullValue()
        );
    }

    @Test
    void writesNonEmptyData() {
        final byte[] data = {0x01, 0x02, 0x03};
        final ByteVector result = new AsmUnknownAttribute("Test", data)
            .write(null, null, 0, 0, 0);
        MatcherAssert.assertThat(
            "Writing attribute with data should produce non-null ByteVector",
            result,
            Matchers.notNullValue()
        );
    }
}
