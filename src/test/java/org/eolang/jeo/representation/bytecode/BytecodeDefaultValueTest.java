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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Default annotation value visitors.
 *
 * @since 0.15.0
 */
final class BytecodeDefaultValueTest {

    @Test
    void skipsDeclinedDefaultValue() {
        Assertions.assertDoesNotThrow(
            () -> new BytecodeDefaultValue(new BytecodePlainAnnotationValue("value", 42))
                .writeTo(new MethodVisitor(Opcodes.ASM9) { }),
            "ASM can decline the annotation default visitor"
        );
    }

    @Test
    void writesAndFinishesAcceptedDefaultValue() {
        final List<Object> events = new ArrayList<>(0);
        new BytecodeDefaultValue(new BytecodePlainAnnotationValue("value", 42)).writeTo(
            new MethodVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotationDefault() {
                    return new AnnotationVisitor(Opcodes.ASM9) {
                        @Override
                        public void visit(final String name, final Object value) {
                            events.add(value);
                        }

                        @Override
                        public void visitEnd() {
                            events.add("end");
                        }
                    };
                }
            }
        );
        MatcherAssert.assertThat(
            "An accepted default value precedes exactly one ending", events,
            Matchers.contains(42, "end")
        );
    }
}
