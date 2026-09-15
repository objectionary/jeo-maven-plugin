/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

/**
 * Scratch.
 * @since 0.1
 */
final class ScratchTest {

    @Test
    void checksWhatAsmGivesUs() {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Foo", null, "java/lang/Object", null);
        final AnnotationVisitor ann = writer.visitAnnotation("LBar;", true);
        final AnnotationVisitor arr = ann.visitArray("ints");
        arr.visit(null, 1);
        arr.visit(null, 2);
        arr.visitEnd();
        ann.visitEnd();
        writer.visitEnd();
        final ClassNode node = new ClassNode();
        new ClassReader(writer.toByteArray()).accept(node, 0);
        node.visibleAnnotations.forEach(
            a -> {
                for (int i = 0; i < a.values.size(); ++i) {
                    final Object val = a.values.get(i);
                    System.out.println(
                        "VALUE[" + i + "] = " + val
                            + " class=" + (val == null ? "null" : val.getClass().getName())
                    );
                }
            }
        );
    }
}
