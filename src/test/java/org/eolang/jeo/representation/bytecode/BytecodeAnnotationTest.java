/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;

/**
 * Annotation visitor lifecycle.
 * @since 0.6
 */
final class BytecodeAnnotationTest {

    @ParameterizedTest
    @MethodSource("writers")
    void finishesEachAcceptedAnnotation(final Consumer<AnnotationVisitor> writer) {
        final AtomicInteger properties = new AtomicInteger();
        final AtomicInteger endings = new AtomicInteger();
        writer.accept(
            new AnnotationVisitor(Opcodes.ASM9) {
                @Override
                public void visit(final String name, final Object value) {
                    MatcherAssert.assertThat(
                        "The property precedes the ending", endings.get(), Matchers.equalTo(0)
                    );
                    properties.incrementAndGet();
                }

                @Override
                public void visitEnd() {
                    MatcherAssert.assertThat(
                        "The ending follows the property", properties.get(), Matchers.equalTo(1)
                    );
                    endings.incrementAndGet();
                }
            }
        );
        MatcherAssert.assertThat(
            "The accepted annotation is finished once", endings.get(), Matchers.equalTo(1)
        );
    }

    @ParameterizedTest
    @MethodSource("writers")
    void skipsDeclinedAnnotations(final Consumer<AnnotationVisitor> writer) {
        Assertions.assertDoesNotThrow(
            () -> writer.accept(null), "A declined annotation has no visitor to finish"
        );
    }

    /**
     * Each supported annotation writer with one property.
     * @return Annotation writers
     */
    private static Stream<Consumer<AnnotationVisitor>> writers() {
        final BytecodeAnnotation annotation = new BytecodeAnnotation(
            "LExample;", true,
            Collections.singletonList(new BytecodePlainAnnotationValue("number", 42))
        );
        return Stream.of(
            visitor -> annotation.write(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                    return visitor;
                }
            }),
            visitor -> annotation.write(new MethodVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                    return visitor;
                }
            }),
            visitor -> annotation.write(0, new MethodVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitParameterAnnotation(
                    final int index, final String desc, final boolean visible
                ) {
                    return visitor;
                }
            }),
            visitor -> annotation.write(new FieldVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                    return visitor;
                }
            }),
            visitor -> annotation.write(new RecordComponentVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                    return visitor;
                }
            }),
            visitor -> annotation.writeTo(new AnnotationVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(final String name, final String desc) {
                    return visitor;
                }
            })
        );
    }
}
