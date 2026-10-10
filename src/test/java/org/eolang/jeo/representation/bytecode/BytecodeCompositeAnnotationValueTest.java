/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Composite annotation values.
 *
 * @since 0.6
 */
final class BytecodeCompositeAnnotationValueTest {

    @ParameterizedTest
    @MethodSource("values")
    void skipsValuesWhenTheVisitorDeclines(final BytecodeAnnotationValue value) {
        Assertions.assertDoesNotThrow(
            () -> value.writeTo(new AnnotationVisitor(Opcodes.ASM9) { }),
            "ASM visitors may decline arrays and nested annotations"
        );
    }

    @ParameterizedTest
    @MethodSource("values")
    void writesAndFinishesAcceptedValues(final BytecodeAnnotationValue value) {
        final AtomicInteger properties = new AtomicInteger();
        final AtomicInteger endings = new AtomicInteger();
        final AnnotationVisitor child = new AnnotationVisitor(Opcodes.ASM9) {
            @Override
            public void visit(final String name, final Object item) {
                properties.incrementAndGet();
            }

            @Override
            public void visitEnd() {
                endings.incrementAndGet();
            }
        };
        value.writeTo(
            new AnnotationVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitArray(final String name) {
                    return child;
                }

                @Override
                public AnnotationVisitor visitAnnotation(
                    final String name, final String descriptor
                ) {
                    return child;
                }
            }
        );
        MatcherAssert.assertThat(
            "An accepted child receives its property", properties.get(), Matchers.equalTo(1)
        );
        MatcherAssert.assertThat(
            "An accepted child is finished exactly once", endings.get(), Matchers.equalTo(1)
        );
    }

    /**
     * Values whose children ASM visitors may skip.
     * @return Array and nested annotation values
     */
    private static Stream<BytecodeAnnotationValue> values() {
        return Stream.of(
            new BytecodeArrayAnnotationValue(
                "items", Collections.singletonList(new BytecodePlainAnnotationValue(null, 42))
            ),
            new BytecodeAnnotationAnnotationValue(
                "nested", "LNested;",
                Collections.singletonList(new BytecodePlainAnnotationValue("number", 42))
            )
        );
    }
}
