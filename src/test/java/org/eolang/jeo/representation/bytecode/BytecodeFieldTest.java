/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.ArrayList;
import java.util.List;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link BytecodeField}.
 *
 * @since 0.15
 */
final class BytecodeFieldTest {

    @Test
    void encodesCustomAttributesName() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Custom attribute container names must use the same encoding as annotations",
            new Xembler(
                new BytecodeField(
                    "Φ",
                    "I",
                    null,
                    0,
                    Opcodes.ACC_PUBLIC,
                    new BytecodeAnnotations(),
                    new BytecodeAttributes(
                        new BytecodeUnknownAttribute("CustomField", new byte[] {1})
                    )
                ).directives(new Format())
            ).xml(),
            XhtmlMatchers.hasXPath("/o/o[@name='attributes-%CE%A6']")
        );
    }

    @Test
    void rejectsControlCharacterInName() {
        MatcherAssert.assertThat(
            "We expect a descriptive error for a field name that XML can't hold",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new BytecodeField(
                    String.format("bad%cname", (char) 1), "I", null, 0, Opcodes.ACC_PUBLIC
                ).directives(new Format())
            ).getMessage(),
            Matchers.containsString("U+0001")
        );
    }

    @Test
    void encodesAnnotationsNameOfFieldWithUnicodeName() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Annotations name of the field contains a character that phi grammar forbids",
            new Xembler(
                new BytecodeField(
                    "Φ",
                    "I",
                    null,
                    0,
                    Opcodes.ACC_PRIVATE,
                    new BytecodeAnnotations(new BytecodeAnnotation("Ljava/lang/Deprecated;", true))
                ).directives(new Format())
            ).xml(),
            XhtmlMatchers.hasXPath("/o/o[@name='annotations-%CE%A6']")
        );
    }

    @Test
    void finishesAcceptedFieldAfterItsAnnotations() {
        final List<String> events = new ArrayList<>(2);
        final FieldVisitor field = new FieldVisitor(Opcodes.ASM9) {
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
        new BytecodeField(
            "number", "I", null, null, 0,
            new BytecodeAnnotations(new BytecodeAnnotation("LExample;", true))
        ).write(
            new ClassVisitor(Opcodes.ASM9) {
                @Override
                public FieldVisitor visitField(
                    final int access, final String name, final String descriptor,
                    final String signature, final Object value
                ) {
                    return field;
                }
            }
        );
        MatcherAssert.assertThat(
            "The field does not end once after its annotations",
            events, Matchers.contains("annotation", "end")
        );
    }

    @Test
    void skipsFieldWhenVisitorDeclines() {
        Assertions.assertDoesNotThrow(
            () -> new BytecodeField(
                "number", "I", null, null, 0,
                new BytecodeAnnotations(new BytecodeAnnotation("LExample;", true))
            ).write(new ClassVisitor(Opcodes.ASM9) { }),
            "A class visitor cannot skip a field"
        );
    }
}
