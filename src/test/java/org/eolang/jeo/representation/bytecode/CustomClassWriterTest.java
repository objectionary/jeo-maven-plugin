/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.lang.reflect.Field;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;

/**
 * Test case for {@link CustomClassWriter}.
 *
 * @since 0.15.0
 */
final class CustomClassWriterTest {

    @Test
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    void putsTheComputationModeBackWhenAVisitFails() throws ReflectiveOperationException {
        final CustomClassWriter writer = new CustomClassWriter();
        final Field inner = CustomClassWriter.class.getDeclaredField("writer");
        inner.setAccessible(true);
        final Object delegate = inner.get(writer);
        final Field compute = ClassWriter.class.getDeclaredField("compute");
        compute.setAccessible(true);
        final int before = compute.getInt(delegate);
        Assertions.assertThrows(
            Throwable.class,
            () -> writer.visitMethod(0, "m", null, null, new String[0], true),
            "a method visit with no descriptor cannot succeed"
        );
        MatcherAssert.assertThat(
            "a failed visit must leave the computation mode where it was",
            compute.getInt(delegate),
            Matchers.equalTo(before)
        );
    }
}
