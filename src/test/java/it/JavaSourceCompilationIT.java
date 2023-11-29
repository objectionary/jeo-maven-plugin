package it;

import com.github.lombrozo.jsmith.RandomJavaClass;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.eolang.jeo.representation.BytecodeRepresentation;
import org.eolang.jeo.representation.EoRepresentation;
import org.eolang.jeo.representation.bytecode.Bytecode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Set of integration tests for Java source parsing and transformation.
 * The default pipeline for all the tests is:
 * - Generate random Java source code
 * - Compile it into bytecode
 * - Transform bytecode into XMIR
 * - Transform XMIR back into bytecode
 * - Compare the original bytecode with the transformed one.
 * @since 0.1
 */
class JavaSourceCompilationIT {

    @Test
    void transformsRandomJavaSourceCodeIntoEoAndBack(@TempDir final Path temp) throws IOException {
        final Bytecode expected = JavaSourceCompilationIT.compile(temp, new RandomJavaClass());
        MatcherAssert.assertThat(
            "Bytecode is not equal to the original one, check that transformation is correct and does not change the bytecode",
            new EoRepresentation(
                new BytecodeRepresentation(expected).toEO()
            ).toBytecode(),
            Matchers.equalTo(expected)
        );
    }

    /**
     * Compile random java class into bytecode.
     * @param where Where to compile.
     * @param clazz Random java class.
     * @return Bytecode.
     */
    private static Bytecode compile(
        final Path where,
        final RandomJavaClass clazz
    ) throws IOException {
        final Path src = where.resolve(String.format("%s.java", clazz.name()));
        Files.write(src, clazz.src().getBytes(StandardCharsets.UTF_8));
        ToolProvider.getSystemJavaCompiler().run(System.in, System.out, System.err, src.toString());
        return new Bytecode(
            Files.readAllBytes(where.resolve(String.format("%s.class", clazz.name())))
        );
    }
}