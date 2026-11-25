/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

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

    @Test
    void derivesCodeFlagFromCodeAttributeOffsetOnRead() {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC,
            "example/AttrSample",
            null,
            "java/lang/Object",
            null
        );
        writer.visitAttribute(new AsmUnknownAttribute("Custom", new byte[]{0x0A}));
        writer.visitEnd();
        final ClassReader reader = new ClassReader(writer.toByteArray());
        MatcherAssert.assertThat(
            "Code flag must be derived from caoffset (-1 outside Code, >= 0 inside Code)",
            !new AsmUnknownAttribute("Custom", true)
                .read(reader, 0, 1, null, -1, null).isCodeAttribute()
                && new AsmUnknownAttribute("Custom", false)
                    .read(reader, 0, 1, null, 0, null).isCodeAttribute(),
            Matchers.is(true)
        );
    }
}
