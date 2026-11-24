/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.representation;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Test case proving that disassembly of a large class fits into a modest heap.
 *
 * <p>A synthetic class with many instructions is disassembled in a child JVM
 * with a constrained heap. Before
 * <a href="https://github.com/objectionary/jeo-maven-plugin/issues/1875">#1875</a>
 * was fixed, the same disassembly on the same heap failed with
 * {@link OutOfMemoryError}, because the whole XMIR was materialized several
 * times (DOM, string, DOM again) during the conversion.</p>
 *
 * @since 0.15.0
 */
final class HeapBoundDisassemblyTest {

    /**
     * The heap of the child JVM.
     * Large enough for a single DOM built from the synthetic class,
     * but not enough for the old implementation, which kept several
     * full copies of the XMIR in memory at the same time.
     */
    private static final String HEAP = "-Xmx384m";

    /**
     * How many milliseconds the child JVM is allowed to run.
     */
    private static final long TIMEOUT_MILLIS = 120_000L;

    @Test
    void disassemblesLargeClassUnderConstrainedHeap() throws Exception {
        final Path clazz = Files.createTempFile("HeapBoundDisassembly", ".class");
        try {
            Files.write(clazz, HeapBoundDisassemblyTest.syntheticClass());
            final Process process = new ProcessBuilder(
                HeapBoundDisassemblyTest.javaExecutable(),
                HeapBoundDisassemblyTest.HEAP,
                "-Xms32m",
                "-cp",
                System.getProperty("java.class.path"),
                "org.eolang.jeo.representation.SubprocessDisassembler",
                clazz.toString()
            ).redirectErrorStream(true).start();
            final String output;
            try (InputStream stream = process.getInputStream()) {
                output = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
            MatcherAssert.assertThat(
                String.format(
                    "Disassembly must finish successfully within %d ms under %s heap. Output:%n%s",
                    HeapBoundDisassemblyTest.TIMEOUT_MILLIS,
                    HeapBoundDisassemblyTest.HEAP,
                    output
                ),
                process.waitFor(
                    HeapBoundDisassemblyTest.TIMEOUT_MILLIS,
                    TimeUnit.MILLISECONDS
                ) && process.exitValue() == 0,
                Matchers.equalTo(true)
            );
        } finally {
            Files.deleteIfExists(clazz);
        }
    }

    private static byte[] syntheticClass() {
        final int methods = 10;
        final int instructions = 20_000;
        final ClassWriter writer = new ClassWriter(0);
        writer.visit(
            Opcodes.V1_8, Opcodes.ACC_PUBLIC, "example/HeapBound",
            null, "java/lang/Object", null
        );
        for (int method = 0; method < methods; ++method) {
            final MethodVisitor visitor = writer.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                String.format("large%03d", method),
                "()V",
                null,
                null
            );
            visitor.visitCode();
            for (int index = 0; index < instructions; ++index) {
                visitor.visitInsn(Opcodes.NOP);
            }
            visitor.visitInsn(Opcodes.RETURN);
            visitor.visitMaxs(0, 1);
            visitor.visitEnd();
        }
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static String javaExecutable() {
        final String name;
        if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            name = "java.exe";
        } else {
            name = "java";
        }
        return Paths.get(System.getProperty("java.home"), "bin", name).toString();
    }
}
