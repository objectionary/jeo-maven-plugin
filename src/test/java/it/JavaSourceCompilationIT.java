/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package it;

import com.github.lombrozo.jsmith.RandomJavaClass;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.tools.ToolProvider;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.XmirRepresentation;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.io.TempDir;

/**
 * Set of integration tests for Java source parsing and transformation.
 * The default pipeline for all the tests is:
 * - Generate random Java source code
 * - Compile it into bytecode
 * - Transform bytecode into XMIR
 * - Transform XMIR back into bytecode
 * - Compare the original bytecode with the transformed one.
 * In this class we compare bytecode as strings because the order of values in
 * the bytecode constant pool might differ, however, the bytecode is still the same.
 * Moreover, we don't care about line numbers and other supplementary information.
 * We ignore it by using `-g:vars` compiler option.
 * @since 0.1
 */
final class JavaSourceCompilationIT {

    @Test
    @EnabledIf(value = "hasJavaCompiler", disabledReason = "Java compiler is not available")
    void transformsRandomJavaSourceCodeIntoEoAndBack(@TempDir final Path temp) throws IOException {
        final Bytecode expected = JavaSourceCompilationIT.compile(temp);
        MatcherAssert.assertThat(
            "Bytecode is not equal to the original one, check that transformation is correct and does not change the bytecode",
            new XmirRepresentation(new BytecodeRepresentation(expected).toXmir())
                .toBytecode()
                .toString(),
            Matchers.equalTo(expected.toString())
        );
    }

    /**
     * Compile random java class into bytecode.
     * @param where Where to compile.
     * @return Bytecode.
     */
    private static Bytecode compile(final Path where) throws IOException {
        final Path java = where.resolve("HelloWorld.java");
        final RandomJavaClass rand = new RandomJavaClass();
        final String src = rand.src();
        Files.write(
            java,
            src.getBytes(StandardCharsets.UTF_8)
        );
        ToolProvider.getSystemJavaCompiler().run(
            System.in,
            System.out,
            System.err,
            "-g:vars",
            "-source", "11",
            "-parameters",
            java.toString()
        );
        return new Bytecode(
            Files.readAllBytes(
                Files.find(where, 1, (path, attr) -> path.toString().endsWith(".class"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No class file found"))
            )
        );
    }

    /**
     * Check if java compiler is available.
     * Don't care about PMD warning: this method is used in @EnabledIf annotation.
     * @return True if java compiler is available.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static boolean hasJavaCompiler() {
        return ToolProvider.getSystemJavaCompiler() != null;
    }
}
