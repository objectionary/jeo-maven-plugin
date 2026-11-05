/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;

/**
 * Annotation visitor lifecycle.
 *
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

    @ParameterizedTest
    @MethodSource("models")
    void staysUsableAsAMapKeyAfterTheInputChanges(
        final String label, final Object model, final Runnable change
    ) {
        final Map<Object, String> cache = new HashMap<>();
        cache.put(model, "original");
        change.run();
        MatcherAssert.assertThat(
            String.format("Changing the caller's list corrupted the %s map key", label),
            cache.get(model), Matchers.equalTo("original")
        );
    }

    private static <T> Arguments sample(
        final String label, final Function<List<T>, Object> factory, final T element
    ) {
        final List<T> values = new ArrayList<>(Collections.singleton(element));
        return Arguments.of(label, factory.apply(values), (Runnable) values::clear);
    }

    private static Stream<Arguments> models() {
        return Stream.of(
            BytecodeAnnotationTest.<BytecodeAnnotationValue>sample(
                "annotation",
                v -> new BytecodeAnnotation("LExample;", true, v),
                new BytecodePlainAnnotationValue("answer", 42)
            ),
            BytecodeAnnotationTest.<BytecodeAnnotationValue>sample(
                "nested annotation",
                v -> new BytecodeAnnotationAnnotationValue("a", "LExample;", v),
                new BytecodePlainAnnotationValue("answer", 42)
            ),
            BytecodeAnnotationTest.<BytecodeAnnotationValue>sample(
                "array annotation",
                v -> new BytecodeArrayAnnotationValue("a", v),
                new BytecodePlainAnnotationValue("answer", 42)
            ),
            BytecodeAnnotationTest.<BytecodeAnnotationValue>sample(
                "type annotation",
                v -> new BytecodeTypeAnnotation(0, TypePath.fromString(""), "LExample;", true, v),
                new BytecodePlainAnnotationValue("answer", 42)
            )
        );
    }

    private static Stream<Consumer<AnnotationVisitor>> writers() {
        final BytecodeAnnotation annotation = new BytecodeAnnotation(
            "LExample;", true,
            Collections.singletonList(new BytecodePlainAnnotationValue("number", 42))
        );
        return Stream.of(
            visitor -> annotation.write(
                new ClassVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(
                        final String desc, final boolean visible
                    ) {
                        return visitor;
                    }
                }
            ),
            visitor -> annotation.write(
                new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(
                        final String desc, final boolean visible
                    ) {
                        return visitor;
                    }
                }
            ),
            visitor -> annotation.write(
                0, new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitParameterAnnotation(
                        final int index, final String desc, final boolean visible
                    ) {
                        return visitor;
                    }
                }
            ),
            visitor -> annotation.write(
                new FieldVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(
                        final String desc, final boolean visible
                    ) {
                        return visitor;
                    }
                }
            ),
            visitor -> annotation.write(
                new RecordComponentVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(
                        final String desc, final boolean visible
                    ) {
                        return visitor;
                    }
                }
            ),
            visitor -> annotation.writeTo(
                new AnnotationVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(final String name, final String desc) {
                        return visitor;
                    }
                }
            )
        );
    }
}
