/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import com.jcabi.xml.XMLDocument;
import org.eolang.jeo.representation.asm.AsmProgram;
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
import org.objectweb.asm.tree.ClassNode;
import org.xembly.Xembler;

/**
 * Test case for the annotable parameter count of a method.
 * @since 0.15.0
 */
final class AnnotableParametersTest {

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

    /**
     * A class whose only method has a shorter parameter annotations table.
     * @return Bytes of the class
     */
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

    /**
     * The invisible annotable parameter count of the only method of the class.
     * @param bytes Bytes of the class
     * @return The count
     */
    private int count(final byte[] bytes) {
        final ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, 0);
        return node.methods.get(0).invisibleAnnotableParameterCount;
    }
}
