/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;

/**
 * Test case for {@link BytecodeRecordComponent}.
 *
 * @since 0.6
 */
final class BytecodeRecordComponentTest {

    @Test
    void finishesAcceptedComponentAfterItsAnnotations() {
        final List<String> events = new ArrayList<>(2);
        final RecordComponentVisitor component = new RecordComponentVisitor(Opcodes.ASM9) {
            @Override
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                events.add("annotation");
                return null;
            }

            @Override
            public void visitEnd() {
                events.add("end");
            }
        };
        new BytecodeRecordComponent(
            "number", "I", null,
            new BytecodeAnnotations(new BytecodeAnnotation("LExample;", true)),
            new BytecodeTypeAnnotations()
        ).write(
            new ClassVisitor(Opcodes.ASM9) {
                @Override
                public RecordComponentVisitor visitRecordComponent(
                    final String name, final String descriptor, final String signature
                ) {
                    return component;
                }
            }
        );
        MatcherAssert.assertThat(
            "The record component does not end once after its annotations",
            events, Matchers.contains("annotation", "end")
        );
    }

    @Test
    void skipsComponentWhenVisitorDeclines() {
        Assertions.assertDoesNotThrow(
            () -> new BytecodeRecordComponent(
                "number", "I", null,
                new BytecodeAnnotations(new BytecodeAnnotation("LExample;", true)),
                new BytecodeTypeAnnotations()
            ).write(new ClassVisitor(Opcodes.ASM9) { }),
            "A class visitor cannot skip a record component"
        );
    }
}
