/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link AsmUnknownAttribute}.
 *
 * @since 0.16.0
 */
final class AsmUnknownAttributeTest {

    @Test
    void writesNullDataAsEmpty() {
        MatcherAssert.assertThat(
            "Writing attribute with null data should produce empty ByteVector, not throw",
            new AsmUnknownAttribute("Test", null).write(null, null, 0, 0, 0),
            Matchers.notNullValue()
        );
    }

    @Test
    void writesNonEmptyData() {
        MatcherAssert.assertThat(
            "Writing attribute with data should produce non-null ByteVector",
            new AsmUnknownAttribute("Test", new byte[]{0x01, 0x02, 0x03})
                .write(null, null, 0, 0, 0),
            Matchers.notNullValue()
        );
    }

    @Test
    void reportsFalseForNonCodeAttributeByDefault() {
        MatcherAssert.assertThat(
            "Regular unknown attributes must report isCodeAttribute() == false by default",
            new AsmUnknownAttribute("ModuleResolution").isCodeAttribute(),
            Matchers.is(false)
        );
    }

    @Test
    void reportsCodeAttributeFlagWhenEnabled() {
        MatcherAssert.assertThat(
            "Code-level unknown attributes must report isCodeAttribute() == true",
            new AsmUnknownAttribute("CharacterRangeTable", true).isCodeAttribute(),
            Matchers.is(true)
        );
    }
}
