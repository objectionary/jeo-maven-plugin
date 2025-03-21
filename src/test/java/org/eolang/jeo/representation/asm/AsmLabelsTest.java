/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import java.util.UUID;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Label;

/**
 * Test case for {@link AsmLabels}.
 * @since 0.6
 */
final class AsmLabelsTest {

    @Test
    void retrievesLabelIfItAbsent() {
        final String id = UUID.randomUUID().toString();
        MatcherAssert.assertThat(
            String.format("Label with id '%s' was not retrieved", id),
            new AsmLabels().label(new BytecodeLabel(id)),
            Matchers.notNullValue()
        );
    }

    @Test
    void retrievesTheSameLabelIfItExists() {
        final String id = UUID.randomUUID().toString();
        final AsmLabels all = new AsmLabels();
        final Label first = all.label(new BytecodeLabel(id));
        final Label second = all.label(new BytecodeLabel(id));
        MatcherAssert.assertThat(
            String.format(
                "We should retrieve the same label if it exists, but labels '%s' and '%s' are different",
                first,
                second
            ),
            first,
            Matchers.sameInstance(second)
        );
    }
}
