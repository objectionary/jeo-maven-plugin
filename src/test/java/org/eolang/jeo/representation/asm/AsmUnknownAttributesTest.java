/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link AsmUnknownAttributes}.
 *
 * @since 0.18.0
 */
final class AsmUnknownAttributesTest {

    @Test
    void doesNotCopyAttributesThatPointIntoConstantPool() {
        MatcherAssert.assertThat(
            "ModuleTarget holds a constant pool index, its raw bytes must not be copied",
            Arrays.stream(AsmUnknownAttributes.prototypes())
                .map(attribute -> attribute.type)
                .collect(Collectors.toList()),
            Matchers.not(Matchers.hasItem("ModuleTarget"))
        );
    }
}
