/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation.asm;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

/**
 * Test case for {@link AsmParameters}.
 *
 * @since 0.6
 */
final class AsmParametersTest {

    @Test
    void keepsParameterNamesInShortMode() {
        final ClassNode node = new ClassNode();
        new ClassReader(
            new AsmProgram(this.classWithNamedParameter()).bytecode().bytecode().bytes()
        ).accept(node, 0);
        MatcherAssert.assertThat(
            "the MethodParameters attribute must survive the default mode",
            node.methods.get(0).parameters.get(0).name,
            Matchers.equalTo("amount")
        );
    }

    private byte[] classWithNamedParameter() {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC,
            "Foo",
            null,
            "java/lang/Object",
            null
        );
        final MethodVisitor method = writer.visitMethod(
            Opcodes.ACC_PUBLIC, "sum", "(I)V", null, null
        );
        method.visitParameter("amount", 0);
        method.visitCode();
        method.visitInsn(Opcodes.RETURN);
        method.visitMaxs(1, 2);
        method.visitEnd();
        writer.visitEnd();
        return writer.toByteArray();
    }
}
