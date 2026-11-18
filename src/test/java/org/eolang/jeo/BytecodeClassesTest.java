/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.XmirRepresentation;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.eolang.jeo.representation.directives.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Tests for {@link BytecodeClasses}.
 *
 * @since 0.14.0
 */
final class BytecodeClassesTest {

    @Test
    void convertsToString() {
        final Path path = Paths.get("src/test/resources/bytecode-classes");
        MatcherAssert.assertThat(
            "BytecodeClasses toString() should return the correct path",
            new BytecodeClasses(path).toString(),
            Matchers.equalTo(path.toString())
        );
    }

    @Test
    void throwsDescriptiveExceptionWhenConvertingNullInputToString() {
        MatcherAssert.assertThat(
            "BytecodeClasses toString() should describe the missing classes directory",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new BytecodeClasses(null).toString()
            ).getMessage(),
            Matchers.containsString("The classes directory is not set")
        );
    }

    @Test
    void throwsDescriptiveExceptionWhenGettingRootFromNullInput() {
        MatcherAssert.assertThat(
            "BytecodeClasses root() should describe the missing classes directory",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new BytecodeClasses(null).root()
            ).getMessage(),
            Matchers.containsString("The classes directory is not set")
        );
    }

    @Test
    void doesNotThrowExceptionOnAbsentDirectory() {
        MatcherAssert.assertThat(
            "BytecodeClasses should handle empty directories without throwing exceptions",
            new BytecodeClasses(Paths.get("/dev/null/absent")).all().count(),
            Matchers.equalTo(0L)
        );
    }

    @Test
    void rejectsInconsistentStackMapTableInVerifiedBytecode() {
        MatcherAssert.assertThat(
            "VerifiedBytecode must reject a class with an inconsistent StackMapTable frame",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new VerifiedBytecode(
                    BytecodeClassesTest.stackMapClass("Bf", "java/lang/Integer")
                ).verify()
            ).getCause(),
            Matchers.instanceOf(VerifyError.class)
        );
    }

    @Test
    void rejectsInconsistentStackMapTableFromDirectory(@TempDir final Path tmp) throws IOException {
        Files.write(
            tmp.resolve("Broken.class"),
            BytecodeClassesTest.stackMapClass("Broken", "java/lang/Integer")
        );
        MatcherAssert.assertThat(
            "BytecodeClasses.verify() must reject a class file whose StackMapTable fails JVM verification",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new BytecodeClasses(tmp).verify()
            ).getMessage(),
            Matchers.containsString("Broken")
        );
    }

    @Test
    void rejectsBrokenStackMapTableAfterXmirRoundTrip() {
        final byte[] broken = new XmirRepresentation(
            new BytecodeRepresentation(
                new Bytecode(BytecodeClassesTest.stackMapClass("RoundTrip", "java/lang/Integer"))
            ).toXmir(new Format(Format.MODE, "debug"))
        ).toBytecode().bytes();
        MatcherAssert.assertThat(
            "Broken StackMapTable preserved through debug XMIR round trip must fail verification",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> new VerifiedBytecode(broken).verify()
            ).getCause(),
            Matchers.instanceOf(VerifyError.class)
        );
    }

    @Test
    void acceptsValidStackMapTable() {
        Assertions.assertDoesNotThrow(
            () -> new VerifiedBytecode(
                BytecodeClassesTest.stackMapClass("Valid", "[Ljava/lang/String;")
            ).verify(),
            "A class with a consistent StackMapTable frame must pass verification"
        );
    }

    private static byte[] stackMapClass(final String name, final String local) {
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC,
            name,
            null,
            "java/lang/Object",
            null
        );
        final MethodVisitor method = writer.visitMethod(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
            "main",
            "([Ljava/lang/String;)V",
            null,
            null
        );
        method.visitCode();
        final Label label = new Label();
        method.visitInsn(Opcodes.ICONST_0);
        method.visitJumpInsn(Opcodes.IFEQ, label);
        method.visitInsn(Opcodes.NOP);
        method.visitLabel(label);
        method.visitFrame(Opcodes.F_FULL, 1, new Object[]{local}, 0, new Object[0]);
        method.visitInsn(Opcodes.RETURN);
        method.visitMaxs(1, 1);
        method.visitEnd();
        writer.visitEnd();
        return writer.toByteArray();
    }
}
