/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.bytecode;

import com.jcabi.xml.XMLDocument;
import java.util.ArrayList;
import java.util.Collections;
import org.eolang.jeo.representation.asm.AsmProgram;
import org.eolang.jeo.representation.directives.DirectivesAnnotation;
import org.eolang.jeo.representation.directives.DirectivesAnnotations;
import org.eolang.jeo.representation.directives.DirectivesMethodParam;
import org.eolang.jeo.representation.directives.DirectivesMethodParams;
import org.eolang.jeo.representation.directives.Format;
import org.eolang.jeo.representation.xmir.XmlObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Bytecode method parameters.
 *
 * @since 0.6
 */
final class BytecodeMethodParametersTest {

    @Test
    void convertsToDirectivesWithTwoParams() throws ImpossibleModificationException {
        final Format format = new Format();
        MatcherAssert.assertThat(
            "Can't convert bytecode method parameters to correct directives",
            new Xembler(
                new BytecodeMethodParameters(
                    new BytecodeMethodParameter(0, Type.INT_TYPE),
                    new BytecodeMethodParameter(1, Type.INT_TYPE)
                ).directives(format)
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesMethodParams(
                        new DirectivesMethodParam(
                            format, 0, null, 0, Type.INT_TYPE
                        ),
                        new DirectivesMethodParam(
                            format, 1, null, 0, Type.INT_TYPE
                        )
                    )
                ).xml()
            )
        );
    }

    @Test
    void convertsMethodParamAnnotationsToDirectives() throws ImpossibleModificationException {
        final String descriptor = "Ljava/lang/Deprecated()";
        final boolean visible = true;
        final Format format = new Format();
        final int index = 0;
        MatcherAssert.assertThat(
            "We expect a visible annotation to be converted to xmir",
            new Xembler(
                new BytecodeMethodParameters(
                    new ArrayList<>(index),
                    Collections.singletonList(
                        new BytecodeParamAnnotations(
                            index,
                            new BytecodeAnnotations(
                                new BytecodeAnnotation(descriptor, visible)
                            )
                        )
                    )
                ).directives(format)
            ).xml(),
            Matchers.equalTo(
                new Xembler(
                    new DirectivesMethodParams(
                        Collections.emptyList(),
                        new DirectivesAnnotations(
                            Collections.singletonList(
                                new DirectivesAnnotation(index, format, descriptor, visible)
                            ),
                            "param-annotations-0"
                        )
                    )
                ).xml()
            )
        );
    }

    @Test
    void keepsTheAnnotableParameterCountThroughTheRoundTrip() throws Exception {
        MatcherAssert.assertThat(
            "the table must keep the number of parameters it had",
            this.count(
                new XmlObject(
                    new XMLDocument(
                        new Xembler(
                            new AsmProgram(this.original()).bytecode(0).directives(new Format())
                        ).xml()
                    )
                ).bytecode().bytecode().bytes()
            ),
            Matchers.equalTo(1)
        );
    }

    @Test
    void readsTheAnnotableParameterCountFromTheClass() throws Exception {
        MatcherAssert.assertThat(
            "the original class must carry the count we put in it",
            this.count(this.original()),
            Matchers.equalTo(1)
        );
    }

    private byte[] original() {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Foo", null, "java/lang/Object", null);
        final MethodVisitor method = writer.visitMethod(
            Opcodes.ACC_PUBLIC, "bar", "(Ljava/lang/Object;I)V", null, null
        );
        method.visitAnnotableParameterCount(1, false);
        final AnnotationVisitor annotation = method.visitParameterAnnotation(
            0, "Ljava/lang/Deprecated;", false
        );
        annotation.visitEnd();
        method.visitCode();
        method.visitInsn(Opcodes.RETURN);
        method.visitMaxs(1, 3);
        method.visitEnd();
        writer.visitEnd();
        return writer.toByteArray();
    }

    private int count(final byte[] bytes) {
        final ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, 0);
        return node.methods.get(0).invisibleAnnotableParameterCount;
    }
}
