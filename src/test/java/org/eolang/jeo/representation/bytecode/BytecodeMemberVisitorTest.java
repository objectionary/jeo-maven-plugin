/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;

/**
 * Lifecycle of visitors accepted for members of a class.
 *
 * @since 0.6
 */
final class BytecodeMemberVisitorTest {

    @ParameterizedTest
    @MethodSource("writers")
    void finishesAcceptedMembers(final Consumer<ClassVisitor> writer) {
        final List<String> events = new ArrayList<>(2);
        writer.accept(new BytecodeMemberVisitorTest.RecordingVisitor(events));
        MatcherAssert.assertThat(
            "The member ends once after its contents", events, Matchers.contains("member", "end")
        );
    }

    @ParameterizedTest
    @MethodSource("writers")
    void skipsDeclinedMembers(final Consumer<ClassVisitor> writer) {
        Assertions.assertDoesNotThrow(
            () -> writer.accept(new ClassVisitor(Opcodes.ASM9) { }),
            "A class visitor may skip a field, record component, or module"
        );
    }

    private static Stream<Consumer<ClassVisitor>> writers() {
        final BytecodeAnnotations annotations = new BytecodeAnnotations(
            new BytecodeAnnotation("LExample;", true)
        );
        return Stream.of(
            visitor -> new BytecodeField("number", "I", null, null, 0, annotations).write(visitor),
            visitor -> new BytecodeRecordComponent(
                "number", "I", null, annotations, new BytecodeTypeAnnotations()
            ).write(visitor),
            visitor -> new BytecodeModule(
                "example", 0, null, "Example", Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList()
            ).write(visitor)
        );
    }

    /**
     * A class visitor recording the contents and ending of each child.
     *
     * @since 0.6
     */
    private static final class RecordingVisitor extends ClassVisitor {

        /**
         * Recorded callbacks.
         */
        private final List<String> events;

        private RecordingVisitor(final List<String> events) {
            super(Opcodes.ASM9);
            this.events = events;
        }

        @Override
        public FieldVisitor visitField(
            final int access, final String name, final String descriptor,
            final String signature, final Object value
        ) {
            return new FieldVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(
                    final String desc, final boolean visible
                ) {
                    RecordingVisitor.this.events.add("member");
                    return null;
                }

                @Override
                public void visitEnd() {
                    RecordingVisitor.this.events.add("end");
                }
            };
        }

        @Override
        public RecordComponentVisitor visitRecordComponent(
            final String name, final String descriptor, final String signature
        ) {
            return new RecordComponentVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitAnnotation(
                    final String desc, final boolean visible
                ) {
                    RecordingVisitor.this.events.add("member");
                    return null;
                }

                @Override
                public void visitEnd() {
                    RecordingVisitor.this.events.add("end");
                }
            };
        }

        @Override
        public ModuleVisitor visitModule(
            final String name, final int access, final String version
        ) {
            return new ModuleVisitor(Opcodes.ASM9) {
                @Override
                public void visitMainClass(final String main) {
                    RecordingVisitor.this.events.add("member");
                }

                @Override
                public void visitEnd() {
                    RecordingVisitor.this.events.add("end");
                }
            };
        }
    }
}
