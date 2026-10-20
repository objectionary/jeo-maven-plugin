/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.eolang.jeo.representation.directives.DirectivesTypeAnnotation;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Tests for {@link BytecodeTypeAnnotation}.
 *
 * @since 0.15.0
 */
final class BytecodeTypeAnnotationTest {

    @ParameterizedTest
    @MethodSource("annotations")
    void finishesAcceptedAnnotation(final BytecodeTypeAnnotation annotation) {
        final List<String> events = new ArrayList<>(0);
        annotation.write(
            new RecordComponentVisitor(Opcodes.ASM9) {
                @Override
                public AnnotationVisitor visitTypeAnnotation(
                    final int ref, final TypePath path, final String desc, final boolean visible
                ) {
                    return new AnnotationVisitor(Opcodes.ASM9) {
                        @Override
                        public void visit(final String name, final Object value) {
                            events.add("value");
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
            "An accepted annotation ends exactly once after its values", events,
            Matchers.anyOf(Matchers.contains("end"), Matchers.contains("value", "end"))
        );
    }

    @ParameterizedTest
    @MethodSource("annotations")
    void skipsDeclinedAnnotation(final BytecodeTypeAnnotation annotation) {
        Assertions.assertDoesNotThrow(
            () -> annotation.write(new RecordComponentVisitor(Opcodes.ASM9) { }),
            "A declined type annotation has no visitor to finish"
        );
    }

    @Test
    void convertsToDirectives() throws ImpossibleModificationException {
        final int ref = TypeReference.FIELD;
        final TypePath path = TypePath.fromString("*");
        final String desc = "Lorg/eolang/jeo/Directives;";
        final boolean visible = true;
        final Format format = new Format();
        final BytecodePlainAnnotationValue value = new BytecodePlainAnnotationValue(
            "format", "json"
        );
        final int index = 42;
        MatcherAssert.assertThat(
            "We expect the bytecode type annotation to be converted to directives type annotation",
            new Xembler(
                new BytecodeTypeAnnotation(
                    ref,
                    path,
                    desc,
                    visible,
                    Collections.singletonList(
                        value
                    )
                ).directives(index, format)
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesTypeAnnotation(
                        format,
                        index,
                        ref,
                        path.toString(),
                        desc,
                        visible,
                        Collections.singletonList(
                            value.directives(0, format)
                        )
                    )
                ).xml()
            )
        );
    }

    @Test
    void writesAbsentPathAsEmptyOne() throws ImpossibleModificationException {
        final Format format = new Format();
        MatcherAssert.assertThat(
            "A type annotation right on the type has no path, it must be written as the empty one",
            new Xembler(
                new BytecodeTypeAnnotation(
                    TypeReference.newTypeReference(TypeReference.FIELD).getValue(),
                    null,
                    "LNonNull;",
                    true,
                    Collections.emptyList()
                ).directives(0, format)
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesTypeAnnotation(
                        format,
                        0,
                        TypeReference.newTypeReference(TypeReference.FIELD).getValue(),
                        "",
                        "LNonNull;",
                        true,
                        Collections.emptyList()
                    )
                ).xml()
            )
        );
    }

    private static Stream<BytecodeTypeAnnotation> annotations() {
        return Stream.of(
            Collections.<BytecodeAnnotationValue>emptyList(),
            Collections.<BytecodeAnnotationValue>singletonList(
                new BytecodePlainAnnotationValue("name", "value")
            )
        ).map(values -> new BytecodeTypeAnnotation(0, null, "LExample;", true, values));
    }
}
